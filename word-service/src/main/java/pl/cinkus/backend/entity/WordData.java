package pl.cinkus.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import pl.cinkus.backend.codegen.types.Category;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "word_data",
        indexes = {
                @Index(name = "idx_word_data_owner_id", columnList = "owner_id"),
                @Index(name = "idx_word_data_owner_word", columnList = "owner_id, word")
        }
)
public class WordData {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "owner_id", nullable = false, updatable = false)
    private UUID ownerId;

    @Column(nullable = false, length = 200)
    private String word;

    @Column(nullable = false, length = 200)
    private String translation;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant insertDateTime;

    @Builder.Default
    @Column
    private Category category = Category.NOT_LEARNED;
}