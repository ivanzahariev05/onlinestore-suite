package softuni.bg.supplementsonlinestore.user.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import softuni.bg.supplementsonlinestore.exception.EmailAlreadyInUserException;
import softuni.bg.supplementsonlinestore.exception.UserNotFoundException;
import softuni.bg.supplementsonlinestore.exception.UsernameAlreadyInUseException;
import softuni.bg.supplementsonlinestore.notification.service.NotificationService;
import softuni.bg.supplementsonlinestore.security.MetaDataAuthentication;
import softuni.bg.supplementsonlinestore.transaction.model.Transaction;
import softuni.bg.supplementsonlinestore.transaction.service.TransactionService;
import softuni.bg.supplementsonlinestore.user.model.Role;
import softuni.bg.supplementsonlinestore.user.model.User;
import softuni.bg.supplementsonlinestore.user.repository.UserRepository;
import softuni.bg.supplementsonlinestore.wallet.model.Wallet;
import softuni.bg.supplementsonlinestore.wallet.service.WalletService;
import softuni.bg.supplementsonlinestore.web.dto.EditProfileRequest;
import softuni.bg.supplementsonlinestore.web.dto.RegisterRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final NotificationService notificationService;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, WalletService walletService, TransactionService transactionService, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.notificationService = notificationService;


    }


    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new UsernameAlreadyInUseException("Username is already in use!");
        }
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyInUserException("The email address is already in use!");
        }
        User user  = userInitialize(registerRequest);
        if (userRepository.count() == 0) {
            user.setRole(Role.ADMIN);
        }

        Wallet wallet = walletService.createNewWallet(user);


        user.setWallet(wallet);


        userRepository.save(user);
        notificationService.saveNotificationPreference(user.getId(), true, registerRequest.getEmail());
        try {
            notificationService.sendWelcomeEmail(user.getId());
        } catch (Exception e) {
            log.error("Failed to send welcome email for user ");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));

        return new MetaDataAuthentication(user.getId(), username, user.getPassword(), user.isActive(), user.getRole());
    }

    public User userInitialize(RegisterRequest registerRequest) {
        return User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .role(Role.USER)
                .registrationDate(LocalDateTime.now())
                .ordersCount(0)
                .isActive(true)
                .build();


    }


    public User findById(UUID id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
    }

    @CacheEvict(value = "users", allEntries = true)
    public void editProfile(UUID id, EditProfileRequest editProfile) {
        User user = findById(id);

        if (editProfile.getFirstName() != null && !editProfile.getFirstName().isBlank()) {
            user.setFirstName(editProfile.getFirstName());
        }
        if (editProfile.getLastName() != null && !editProfile.getLastName().isBlank()) {
            user.setLastName(editProfile.getLastName());
        }
        if (editProfile.getImageUrl() != null && !editProfile.getImageUrl().isBlank()) {
            user.setImageUrl(editProfile.getImageUrl());
        }

        userRepository.save(user);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @CacheEvict(value = "users", allEntries = true)
    public void switchRole(UUID id) {
        User user = findById(id);

        if (user.getRole() == Role.USER) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }
        userRepository.save(user);
    }

    public void switchStatus(UUID id) {
        User user = findById(id);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    public List<Transaction> findAllTransactions() {
        return transactionService.findByUser(getCurrentUser().getUsername());
    }

    public User findByUsername(String toUser) {
        Optional<User> byUsername = userRepository.findByUsername(toUser);
        return byUsername.orElseThrow(() -> new UserNotFoundException("User with "+ toUser +" username not found"));
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public void increaseOrdersCount(User user) {
        user.setOrdersCount(user.getOrdersCount() + 1);
    }
}
