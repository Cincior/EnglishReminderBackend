package pl.cinkus.IdentityService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cinkus.IdentityService.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByNickNameOrEmail(String nickName, String email);
    boolean existsByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);
    Optional<User> findByEmail(String email);
}
