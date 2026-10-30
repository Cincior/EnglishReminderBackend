package pl.cinkus.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cinkus.backend.codegen.types.Category;

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
    private Category category;
    private float memorizeChance;
}
