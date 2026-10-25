package pl.cinkus.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cinkus.backend.entity.WordTestResult;

import java.util.UUID;

public interface WordTestResultRepository extends JpaRepository<WordTestResult, UUID> {
}
