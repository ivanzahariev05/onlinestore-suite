package softuni.bg.supplementsonlinestore.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import softuni.bg.supplementsonlinestore.transaction.model.Transaction;
import softuni.bg.supplementsonlinestore.transaction.model.TransactionStatus;
import softuni.bg.supplementsonlinestore.transaction.model.TransactionType;
import softuni.bg.supplementsonlinestore.transaction.repository.TransactionRepository;
import softuni.bg.supplementsonlinestore.transaction.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTransaction_Success() {

        Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(100))
                .owner("User1")
                .sender("User2")
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCEEDED)
                .transactionDate(LocalDateTime.now())
                .failureReason(null)
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);


        Transaction result = transactionService.createTransaction(
                BigDecimal.valueOf(100), "User1", "User2",
                TransactionType.DEPOSIT, TransactionStatus.SUCCEEDED,
                LocalDateTime.now(), null
        );


        assertNotNull(result);
        assertEquals(TransactionStatus.SUCCEEDED, result.getStatus());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testFindByUser() {
        Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(50))
                .owner("User1")
                .sender("User2")
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.SUCCEEDED)
                .transactionDate(LocalDateTime.now())
                .failureReason(null)
                .build();

        when(transactionRepository.findTransactionsByUser("User1")).thenReturn(List.of(transaction));

        List<Transaction> transactions = transactionService.findByUser("User1");

        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
        assertEquals("User1", transactions.get(0).getOwner());
        verify(transactionRepository, times(1)).findTransactionsByUser("User1");
    }

    @Test
    void testUpdateTransaction() {
        Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(200))
                .owner("User1")
                .sender("User3")
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCEEDED)
                .transactionDate(LocalDateTime.now())
                .failureReason(null)
                .build();

        when(transactionRepository.save(transaction)).thenReturn(transaction);

        transactionService.updateTransaction(transaction);

        verify(transactionRepository, times(1)).save(transaction);
    }
}
