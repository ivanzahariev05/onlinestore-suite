package softuni.bg.supplementsonlinestore.wallet.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import softuni.bg.supplementsonlinestore.exception.DomainException;
import softuni.bg.supplementsonlinestore.exception.InsufficientFundsSendMoneyException;
import softuni.bg.supplementsonlinestore.exception.UserNotFoundException;
import softuni.bg.supplementsonlinestore.transaction.model.Transaction;
import softuni.bg.supplementsonlinestore.transaction.model.TransactionStatus;
import softuni.bg.supplementsonlinestore.transaction.model.TransactionType;
import softuni.bg.supplementsonlinestore.transaction.service.TransactionService;
import softuni.bg.supplementsonlinestore.user.model.User;
import softuni.bg.supplementsonlinestore.user.service.UserService;
import softuni.bg.supplementsonlinestore.wallet.model.Wallet;
import softuni.bg.supplementsonlinestore.wallet.repository.WalletRepository;
import softuni.bg.supplementsonlinestore.web.dto.SendToAFriendRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Service
@Slf4j
public class WalletService {
    private final WalletRepository walletRepository;
    private final TransactionService transactionService;
    private final UserService userService;
    private static final String NUTRIBOOST_LTD = "NUTRIBOOST_LTD";

    public WalletService(WalletRepository walletRepository, TransactionService transactionService, @Lazy UserService userService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    public Wallet createNewWallet(User user) {
        Wallet wallet = Wallet.builder()
                .owner(user)
                .balance(BigDecimal.ZERO)
                .currency(Currency.getInstance("EUR"))
                .build();
        return walletRepository.save(wallet);
    }

    public Wallet findWalletById(UUID id) {
        return walletRepository.findWalletById(id)
                .orElseThrow(() -> new DomainException("Wallet not found"));
    }

    public Transaction addFunds(UUID id, BigDecimal amount) {
        Wallet wallet = findWalletById(id);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Amount must be greater than zero");
        }

        charge(wallet, amount, true);

        return transactionService.createTransaction(
                amount,
                wallet.getOwner().getUsername(),
                NUTRIBOOST_LTD,
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCEEDED,
                LocalDateTime.now(),
                null
        );
    }

    @Transactional
    public void sendToAFriend(@Valid SendToAFriendRequest sendToAFriendRequest) {
        log.info("Initiating sendToAFriend: sender={}, receiver={}, amount={}",
                userService.getCurrentUser().getUsername(), sendToAFriendRequest.getToUser(), sendToAFriendRequest.getAmount());

        User receiverUser = userService.findByUsername(sendToAFriendRequest.getToUser());
        if (receiverUser == null) {
            throw new UserNotFoundException("User not found");
        }
        Wallet receiverWallet = receiverUser.getWallet();
        Wallet senderWallet = userService.getCurrentUser().getWallet();

        BigDecimal amount = sendToAFriendRequest.getAmount();

        try {
            charge(senderWallet, amount, false);
            charge(receiverWallet, amount, true);

            transactionService.createTransaction(
                    amount,
                    receiverWallet.getOwner().getUsername(),
                    senderWallet.getOwner().getUsername(),
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.SUCCEEDED,
                    LocalDateTime.now(),
                    null
            );

            transactionService.createTransaction(
                    amount,
                    receiverWallet.getOwner().getUsername(),
                    senderWallet.getOwner().getUsername(),
                    TransactionType.DEPOSIT,
                    TransactionStatus.SUCCEEDED,
                    LocalDateTime.now(),
                    null
            );

            log.info("Funds transferred successfully: sender={}, receiver={}, amount={}",
                    senderWallet.getOwner().getUsername(), receiverWallet.getOwner().getUsername(), amount);
        }catch(InsufficientFundsSendMoneyException e){
                log.warn("Transaction failed: {}", e.getMessage());

                transactionService.createTransaction(
                        amount,
                        receiverWallet.getOwner().getUsername(),
                        senderWallet.getOwner().getUsername(),
                        TransactionType.WITHDRAWAL,
                        TransactionStatus.FAILED,
                        LocalDateTime.now(),
                        "Insufficient funds"
                );

                transactionService.createTransaction(
                        amount,
                        receiverWallet.getOwner().getUsername(),
                        senderWallet.getOwner().getUsername(),
                        TransactionType.DEPOSIT,
                        TransactionStatus.FAILED,
                        LocalDateTime.now(),
                        "Transfer failed"
                );

                throw e;
        }
    }

            @Transactional
            public void charge (Wallet wallet, BigDecimal amount,boolean isCredit){
                if (!isCredit && wallet.getBalance().compareTo(amount) < 0) {
                    log.warn("Insufficient funds: user={}, balance={}, requested={}",
                            wallet.getOwner().getUsername(), wallet.getBalance(), amount);
                    throw new InsufficientFundsSendMoneyException("Insufficient funds");
                }

                BigDecimal newBalance = isCredit ? wallet.getBalance().add(amount) : wallet.getBalance().subtract(amount);
                wallet.setBalance(newBalance);
                walletRepository.save(wallet);

                log.info("{} wallet balance updated: user={}, new balance={}",
                        isCredit ? "Credited" : "Debited", wallet.getOwner().getUsername(), wallet.getBalance());
            }
        }
