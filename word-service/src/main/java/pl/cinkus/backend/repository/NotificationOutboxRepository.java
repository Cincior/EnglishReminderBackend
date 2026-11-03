package pl.cinkus.backend.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import pl.cinkus.backend.entity.Notification;
import pl.cinkus.backend.entity.NotificationOutbox;

import java.util.List;
import java.util.UUID;

public interface NotificationOutboxRepository extends JpaRepository<NotificationOutbox, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select o
    from NotificationOutbox o
    join fetch o.notification
    where o.isPublished = :isPublished
      and o.isFailed = :isFailed
      and o.isPublishing = :isPublishing
""")
    List<NotificationOutbox> findAllByIsPublishedAndIsFailedAndIsPublishing(
            boolean isPublished,
            boolean isFailed,
            boolean isPublishing
    );
}
