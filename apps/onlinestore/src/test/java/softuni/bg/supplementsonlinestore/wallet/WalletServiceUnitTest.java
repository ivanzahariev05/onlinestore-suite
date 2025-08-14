package softuni.bg.supplementsonlinestore.wallet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import softuni.bg.supplementsonlinestore.exception.InsufficientFundsSendMoneyException;
import softuni.bg.supplementsonlinestore.transaction.model.Transaction;
import softuni.bg.supplementsonlinestore.transaction.model.TransactionStatus;
import softuni.bg.supplementsonlinestore.transaction.service.TransactionService;
import softuni.bg.supplementsonlinestore.user.model.User;
import softuni.bg.supplementsonlinestore.user.service.UserService;
import softuni.bg.supplementsonlinestore.wallet.model.Wallet;
import softuni.bg.supplementsonlinestore.wallet.repository.WalletRepository;
import softuni.bg.supplementsonlinestore.wallet.service.WalletService;
import softuni.bg.supplementsonlinestore.web.dto.SendToAFriendRequest;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserService userService;

    @InjectMocks
    private WalletService walletService;

    @Test
    void createNewWallet_happyPath() throws Exception {
        User user = new User();
        Wallet wallet = new Wallet(user, BigDecimal.ZERO, Currency.getInstance("EUR"));

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet createdWallet = walletService.createNewWallet(user);

        assertNotNull(createdWallet);
        assertEquals(BigDecimal.ZERO, createdWallet.getBalance());
        assertEquals(user, createdWallet.getOwner());

        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void adding20ToWalletWithBalance50_ShouldReturnNewBalance70() {
        UUID walletId = UUID.randomUUID();

        Wallet wallet = new Wallet();
        wallet.setId(walletId);

        User user = new User();
        user.setUsername("testuser");
        wallet.setOwner(user);

        wallet.setBalance(BigDecimal.valueOf(50));

        when(walletRepository.findWalletById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionService.createTransaction(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new Transaction());

        Transaction transaction = walletService.addFunds(walletId, BigDecimal.valueOf(20));

        assertNotNull(transaction);
        assertEquals(BigDecimal.valueOf(70), wallet.getBalance());
    }

    @Test
    void sendToAFriend30WithWalletBalance100_ShouldWorkAndLeftBalanceShouldBe70() {
        User sender = new User();
        sender.setUsername("sender");
        sender.setWallet(new Wallet(sender, BigDecimal.valueOf(100), Currency.getInstance("EUR")));

        User receiver = new User();
        receiver.setUsername("receiver");
        receiver.setWallet(new Wallet(receiver, BigDecimal.valueOf(50), Currency.getInstance("EUR")));

        SendToAFriendRequest request = new SendToAFriendRequest("receiver", BigDecimal.valueOf(30));

        when(userService.getCurrentUser()).thenReturn(sender);
        when(userService.findByUsername("receiver")).thenReturn(receiver);

        walletService.sendToAFriend(request);

        assertEquals(BigDecimal.valueOf(70), sender.getWallet().getBalance());
        assertEquals(BigDecimal.valueOf(80), receiver.getWallet().getBalance());

        verify(transactionService, times(2)).createTransaction(any(), any(), any(), any(), any(), any(), any());
    }
    @Test
    void sendToAFriend30WithwalletBalance10_ShouldThrowException() {
        User sender = new User();
        sender.setUsername("sender");
        sender.setWallet(new Wallet(sender, BigDecimal.valueOf(10), Currency.getInstance("EUR")));

        User receiver = new User();
        receiver.setUsername("receiver");
        receiver.setWallet(new Wallet(receiver, BigDecimal.valueOf(50), Currency.getInstance("EUR")));

        SendToAFriendRequest request = new SendToAFriendRequest("receiver", BigDecimal.valueOf(30));

        when(userService.getCurrentUser()).thenReturn(sender);
        when(userService.findByUsername("receiver")).thenReturn(receiver);

        assertThrows(InsufficientFundsSendMoneyException.class, () -> walletService.sendToAFriend(request));

        assertEquals(BigDecimal.valueOf(10), sender.getWallet().getBalance());
        assertEquals(BigDecimal.valueOf(50), receiver.getWallet().getBalance());

        verify(transactionService, times(2)).createTransaction(any(), any(), any(), any(), eq(TransactionStatus.FAILED), any(), any());
    }




}
