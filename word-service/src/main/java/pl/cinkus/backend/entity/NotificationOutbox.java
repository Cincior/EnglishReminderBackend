package pl.cinkus.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "notification_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_notification_outbox")
    )
    private Notification notification;
    @Builder.Default
    private Integer publishTries = 0;
    private boolean isPublished;
    private boolean isPublishing;
    private boolean isSent;
    private boolean isFailed;
    @CreationTimestamp
    private Instant registeredTime;
}
