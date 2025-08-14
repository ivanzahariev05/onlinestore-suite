package softuni.bg.supplementsonlinestore.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import softuni.bg.supplementsonlinestore.transaction.model.Transaction;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {


        @Query("SELECT t FROM Transaction t WHERE t.owner = :username OR t.sender = :username")
        List<Transaction> findTransactionsByUser(@Param("username") String username);

}
