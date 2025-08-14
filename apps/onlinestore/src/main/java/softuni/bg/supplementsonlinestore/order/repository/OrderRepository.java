package softuni.bg.supplementsonlinestore.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.bg.supplementsonlinestore.order.model.Order;
import softuni.bg.supplementsonlinestore.user.model.User;

import java.util.List;
import java.util.UUID;
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllOrdersByUserId(UUID  id);
}
