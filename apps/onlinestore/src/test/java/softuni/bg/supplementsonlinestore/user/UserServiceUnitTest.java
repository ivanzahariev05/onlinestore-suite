package softuni.bg.supplementsonlinestore.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import softuni.bg.supplementsonlinestore.exception.UserNotFoundException;
import softuni.bg.supplementsonlinestore.exception.UsernameAlreadyInUseException;
import softuni.bg.supplementsonlinestore.notification.service.NotificationService;
import softuni.bg.supplementsonlinestore.transaction.service.TransactionService;
import softuni.bg.supplementsonlinestore.user.model.Role;
import softuni.bg.supplementsonlinestore.user.model.User;
import softuni.bg.supplementsonlinestore.user.repository.UserRepository;
import softuni.bg.supplementsonlinestore.user.service.UserService;
import softuni.bg.supplementsonlinestore.wallet.model.Wallet;
import softuni.bg.supplementsonlinestore.wallet.service.WalletService;
import softuni.bg.supplementsonlinestore.web.dto.EditProfileRequest;
import softuni.bg.supplementsonlinestore.web.dto.RegisterRequest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private WalletService walletService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UserService userService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setActive(true);
    }


    @Test
    void initializingUser_happyPath()  {
        RegisterRequest request = new RegisterRequest("testuser", "test@email.com", "password123");

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        User user = userService.userInitialize(request);

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("test@email.com", user.getEmail());
        assertEquals(Role.USER, user.getRole());
        assertTrue(user.isActive());
        assertEquals(0, user.getOrdersCount());
    }

    @Test
    void firstUserRegister_shouldMakeHimAdmin() {
        RegisterRequest request = new RegisterRequest("firstuser", "password123", "admin@email.com");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.count()).thenReturn(0L);
        when(walletService.createNewWallet(any(User.class))).thenReturn(new Wallet());

        userService.registerUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertEquals(Role.ADMIN, userCaptor.getValue().getRole());
    }

    @Test
    void creatingUserWithExistingUsername_shouldThrowUsernameAlreadyInUseException() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", "test@email.com");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(new User()));

        assertThrows(UsernameAlreadyInUseException.class, () -> userService.registerUser(request));
    }



    @Test
    void findUSerByIdHappyPath() {
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.findById(userId);

        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
        assertEquals("testuser", foundUser.getUsername());
    }


    @Test
    void editProfileHappyPath() {
        EditProfileRequest request = new EditProfileRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setImageUrl("https://example.com/image.jpg");

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

        userService.editProfile(userId, request);

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("https://example.com/image.jpg", user.getImageUrl());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSwitchRole() {
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

        userService.switchRole(userId);
        assertEquals(Role.ADMIN, user.getRole());

        userService.switchRole(userId);
        assertEquals(Role.USER, user.getRole());
    }
    @Test
    void testSwitchStatus() {
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

        userService.switchStatus(userId);
        assertFalse(user.isActive());

        userService.switchStatus(userId);
        assertTrue(user.isActive());
    }
    @Test
    void testFindByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User foundUser = userService.findByUsername("testuser");

        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
    }

    @Test
    void testFindByUsername_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByUsername("unknown"));
    }


}
