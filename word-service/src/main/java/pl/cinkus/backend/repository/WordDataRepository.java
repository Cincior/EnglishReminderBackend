package pl.cinkus.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cinkus.backend.model.WordData;

import java.util.List;
import java.util.UUID;

public interface WordDataRepository extends JpaRepository<WordData, UUID> {
    List<WordData> findByOwnerId(String ownerId);
    boolean existsByWord(String word);
}
