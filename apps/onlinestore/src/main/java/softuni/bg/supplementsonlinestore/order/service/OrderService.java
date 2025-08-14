package softuni.bg.supplementsonlinestore.order.service;

import org.springframework.stereotype.Service;
import softuni.bg.supplementsonlinestore.order.model.Order;
import softuni.bg.supplementsonlinestore.order.repository.OrderRepository;
import softuni.bg.supplementsonlinestore.product.model.Product;
import softuni.bg.supplementsonlinestore.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders(UUID userId) {
       return orderRepository.findAllOrdersByUserId(userId);
    }

    public Order createNewOrder(User user, BigDecimal totalPrice, LocalDateTime orderDate, Product product) {
        Order order = Order.builder()
                .user(user)
                .totalPrice(totalPrice)
                .orderDate(orderDate)
                .product(product)
                .build();
        return orderRepository.save(order);
    }
}
