package softuni.bg.supplementsonlinestore.wallet.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import softuni.bg.supplementsonlinestore.user.model.User;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallets")
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private BigDecimal balance;

    private Currency currency;

    @OneToOne
    @JsonBackReference
    private User owner;

    public Wallet(User user, BigDecimal zero, Currency eur) {
        this.owner = user;
        this.balance = zero;
        this.currency = eur;
    }
}
