package pl.cinkus.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordDataDTO {
    private String id;
    private String word;
    private String translation;
    private Instant insertDateTime;
    private boolean isLearned;
    private float memorizeChance;
}
