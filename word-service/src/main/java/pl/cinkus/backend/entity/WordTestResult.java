package pl.cinkus.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "word_test_results",
        indexes = {
                @Index(name = "idx_test_results_user_id", columnList = "user_id"),
                @Index(name = "idx_test_results_word_id", columnList = "word_id")
        }
)
public class WordTestResult {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID testId;

    @Column(name = "word_id", nullable = false, updatable = false)
    private UUID wordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "word_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_test_result_word")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private WordData wordData;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false, updatable = false)
    private boolean isCorrect;

    @Column(nullable = false, updatable = false)
    private long testTimeInMilis;
}