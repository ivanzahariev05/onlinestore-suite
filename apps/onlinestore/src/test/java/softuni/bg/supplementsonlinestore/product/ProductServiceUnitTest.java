package softuni.bg.supplementsonlinestore.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import softuni.bg.supplementsonlinestore.exception.InsufficientFundsForPurchaseException;
import softuni.bg.supplementsonlinestore.exception.ProductNotFoundException;
import softuni.bg.supplementsonlinestore.order.service.OrderService;
import softuni.bg.supplementsonlinestore.product.model.Product;
import softuni.bg.supplementsonlinestore.product.model.ProductType;
import softuni.bg.supplementsonlinestore.product.repository.ProductRepository;
import softuni.bg.supplementsonlinestore.product.service.ProductService;
import softuni.bg.supplementsonlinestore.transaction.model.TransactionStatus;
import softuni.bg.supplementsonlinestore.transaction.model.TransactionType;
import softuni.bg.supplementsonlinestore.transaction.service.TransactionService;
import softuni.bg.supplementsonlinestore.user.model.User;
import softuni.bg.supplementsonlinestore.user.service.UserService;
import softuni.bg.supplementsonlinestore.wallet.model.Wallet;
import softuni.bg.supplementsonlinestore.web.dto.ProductRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private TransactionService transactionService;
    @Mock
    private UserService userService;
    @Mock
    private OrderService orderService;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Wallet wallet;
    private User user;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        user = new User();
        user.setUsername("testUser");

        wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100));
        wallet.setOwner(user);

        product = new Product();
        product.setId(productId);
        product.setPrice(BigDecimal.valueOf(20));
        product.setQuantity(10);
    }

    @Test
    void findAllProducts_ShouldReturnList() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> products = productService.findAllProducts();

        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void findByProductType_ShouldReturnFilteredList() {
        when(productRepository.findProductByType(ProductType.SUPPLEMENT)).thenReturn(List.of(product));

        List<Product> products = productService.findByProductType(ProductType.SUPPLEMENT);

        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
        verify(productRepository, times(1)).findProductByType(ProductType.SUPPLEMENT);
    }

    @Test
    void findProductById_ShouldReturnProduct_WhenFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product foundProduct = productService.findProductById(productId);

        assertNotNull(foundProduct);
        assertEquals(productId, foundProduct.getId());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void findProductById_ShouldThrowException_WhenNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findProductById(productId));
    }

    @Test
    void subtractProduct_ShouldDecreaseQuantity() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.subtractProduct(productId, 2);

        assertEquals(8, product.getQuantity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void buyProduct_ShouldCompletePurchase_WhenFundsAreSufficient() {
        ProductRequest request = new ProductRequest();
        request.setQuantity(2);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.buyProduct(productId, request, wallet);

        assertEquals(BigDecimal.valueOf(60), wallet.getBalance());
        assertEquals(8, product.getQuantity());
        verify(transactionService, times(1)).createTransaction(
                eq(BigDecimal.valueOf(40)),
                eq("Nutriboost ltd"),
                eq("testUser"),
                eq(TransactionType.PURCHASE),
                eq(TransactionStatus.SUCCEEDED),
                any(),
                isNull()
        );
        verify(orderService, times(1)).createNewOrder(eq(user), eq(BigDecimal.valueOf(40)), any(), eq(product));
        verify(userService, times(1)).increaseOrdersCount(user);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void buyProduct_ShouldThrowException_WhenFundsAreInsufficient() {
        wallet.setBalance(BigDecimal.valueOf(10));
        ProductRequest request = new ProductRequest();
        request.setQuantity(2);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(InsufficientFundsForPurchaseException.class, () -> productService.buyProduct(productId, request, wallet));

        verify(transactionService, times(1)).createTransaction(
                eq(BigDecimal.valueOf(40)),
                eq("Nutriboost ltd"),
                eq("testUser"),
                eq(TransactionType.PURCHASE),
                eq(TransactionStatus.FAILED),
                any(),
                eq("Insufficient funds")
        );
        verify(orderService, never()).createNewOrder(any(), any(), any(), any());
        verify(userService, never()).increaseOrdersCount(any());
        verify(productRepository, never()).save(any());
    }
}
