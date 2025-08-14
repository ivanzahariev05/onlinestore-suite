package softuni.bg.supplementsonlinestore.user.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import softuni.bg.supplementsonlinestore.order.model.Order;
import softuni.bg.supplementsonlinestore.transaction.model.Transaction;
import softuni.bg.supplementsonlinestore.wallet.model.Wallet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    private String imageUrl;

    private String firstName;

    private String lastName;

    private boolean isActive;

    private int ordersCount;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToMany()
    @JsonManagedReference
    private List<Transaction> transactions;

    @OneToOne(mappedBy = "owner")
    @JsonManagedReference
    private Wallet wallet;



}

