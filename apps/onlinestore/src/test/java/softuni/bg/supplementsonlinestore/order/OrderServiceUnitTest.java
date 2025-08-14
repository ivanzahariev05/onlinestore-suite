package softuni.bg.supplementsonlinestore.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import softuni.bg.supplementsonlinestore.order.model.Order;
import softuni.bg.supplementsonlinestore.order.repository.OrderRepository;
import softuni.bg.supplementsonlinestore.product.model.Product;
import softuni.bg.supplementsonlinestore.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());

        product = new Product();
        product.setId(UUID.randomUUID());

        order = Order.builder()
                .id(UUID.randomUUID())
                .user(user)
                .product(product)
                .totalPrice(BigDecimal.valueOf(100.00))
                .orderDate(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllOrders_ShouldReturnListOfOrders() {
        UUID userId = user.getId();
        when(orderRepository.findAllOrdersByUserId(userId)).thenReturn(List.of(order));

        List<Order> orders = orderService.getAllOrders(userId);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(order, orders.get(0));
        verify(orderRepository, times(1)).findAllOrdersByUserId(userId);
    }

    @Test
    void createNewOrder_ShouldSaveAndReturnOrder() {
        BigDecimal totalPrice = BigDecimal.valueOf(100.00);
        LocalDateTime orderDate = LocalDateTime.now();
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order savedOrder = orderService.createNewOrder(user, totalPrice, orderDate, product);

        assertNotNull(savedOrder);
        assertEquals(user, savedOrder.getUser());
        assertEquals(product, savedOrder.getProduct());
        assertEquals(totalPrice, savedOrder.getTotalPrice());
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
