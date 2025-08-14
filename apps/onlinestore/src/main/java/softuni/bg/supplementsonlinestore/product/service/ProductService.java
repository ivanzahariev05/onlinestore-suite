package softuni.bg.supplementsonlinestore.product.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.bg.supplementsonlinestore.exception.InsufficientFundsForPurchaseException;
import softuni.bg.supplementsonlinestore.exception.ProductNotFoundException;
import softuni.bg.supplementsonlinestore.order.service.OrderService;
import softuni.bg.supplementsonlinestore.product.model.Product;
import softuni.bg.supplementsonlinestore.product.model.ProductType;
import softuni.bg.supplementsonlinestore.product.repository.ProductRepository;
import softuni.bg.supplementsonlinestore.transaction.model.TransactionStatus;
import softuni.bg.supplementsonlinestore.transaction.model.TransactionType;
import softuni.bg.supplementsonlinestore.transaction.service.TransactionService;
import softuni.bg.supplementsonlinestore.user.model.User;
import softuni.bg.supplementsonlinestore.user.service.UserService;
import softuni.bg.supplementsonlinestore.wallet.model.Wallet;
import softuni.bg.supplementsonlinestore.web.dto.ProductRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final TransactionService transactionService;
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public ProductService(ProductRepository productRepository, TransactionService transactionService, UserService userService, OrderService orderService) {
        this.productRepository = productRepository;
        this.transactionService = transactionService;
        this.userService = userService;
        this.orderService = orderService;
    }


    public List<Product> findAllProducts() {

        return productRepository.findAll();
    }
    public List<Product> findByProductType(ProductType type) {
        return productRepository.findProductByType(type);
    }

    public Product findProductById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }
    public void subtractProduct(UUID id,int quantity) {
        Product product = findProductById(id);
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
    }

    @Transactional
    public void buyProduct(UUID id, ProductRequest productRequest, Wallet wallet) {
        Product product = findProductById(id);
        User owner = wallet.getOwner();

        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(productRequest.getQuantity()));
        if (wallet.getBalance().compareTo(totalPrice) >= 0 && totalPrice.compareTo(BigDecimal.ZERO) > 0) {
            wallet.setBalance(wallet.getBalance().subtract(totalPrice));
            transactionService.createTransaction(
                    totalPrice,
                    "Nutriboost ltd",
                    owner.getUsername(),
                    TransactionType.PURCHASE,
                    TransactionStatus.SUCCEEDED,
                    LocalDateTime.now(),
                    null

            );
            orderService.createNewOrder(
                    owner,
                    totalPrice,
                    LocalDateTime.now(),
                    product);
            subtractProduct(id, productRequest.getQuantity());
            userService.increaseOrdersCount(owner);
            if (product.getQuantity() <= 0){
                productRepository.delete(product);
            }
        }else {
            transactionService.createTransaction(
                    totalPrice,
                    "Nutriboost ltd",
                    owner.getUsername(),
                    TransactionType.PURCHASE,
                    TransactionStatus.FAILED,
                    LocalDateTime.now(),
                    "Insufficient funds"
            );
            throw new InsufficientFundsForPurchaseException("Not enough balance");
        }

        



    }
}
