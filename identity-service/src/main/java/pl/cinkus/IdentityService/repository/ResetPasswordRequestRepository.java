package pl.cinkus.IdentityService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cinkus.IdentityService.entity.ResetPasswordRequest;

import java.util.Optional;

public interface ResetPasswordRequestRepository extends JpaRepository<ResetPasswordRequest, String> {
    Optional<ResetPasswordRequest> findByEmail(String email);
}
