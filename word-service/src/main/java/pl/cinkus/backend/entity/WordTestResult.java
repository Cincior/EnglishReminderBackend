package pl.cinkus.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class WordTestResult {
    @Id
    private UUID testId;
    private UUID wordId;
    private UUID userId;
    private boolean isCorrect;
    private Long testTimeInMilis;
}
