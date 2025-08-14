package softuni.bg.supplementsonlinestore.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import softuni.bg.supplementsonlinestore.product.model.Product;
import softuni.bg.supplementsonlinestore.transaction.model.Transaction;
import softuni.bg.supplementsonlinestore.user.model.User;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User user;

    private BigDecimal totalPrice;

    private LocalDateTime orderDate;

    @ManyToOne
    private Product product;


}

