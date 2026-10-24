package pl.cinkus.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cinkus.backend.entity.WordData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WordDataRepository extends JpaRepository<WordData, UUID> {
    List<WordData> findByOwnerId(UUID ownerId);
    Optional<WordData> findFirstByOwnerIdAndWord(UUID ownerId, String word);
    Optional<WordData> findByOwnerIdAndId(UUID ownerId, UUID wordId);
}
