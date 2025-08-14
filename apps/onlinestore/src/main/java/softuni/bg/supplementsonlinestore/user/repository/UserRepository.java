package softuni.bg.supplementsonlinestore.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.bg.supplementsonlinestore.user.model.User;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository  extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(java.lang.String username);

    Optional<User> findByEmail(java.lang.String email);

    Optional<User> findUserById(UUID id);

}
