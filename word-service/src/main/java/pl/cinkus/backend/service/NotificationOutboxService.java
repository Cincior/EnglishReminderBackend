package pl.cinkus.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.cinkus.backend.entity.NotificationOutbox;
import pl.cinkus.backend.repository.NotificationOutboxRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationOutboxService {
    private static final int MAX_TRIES = 5;
    private final NotificationOutboxRepository notificationOutboxRepository;

    @Transactional
    public List<NotificationOutbox> claimPendingNotifications() {
        List<NotificationOutbox> outboxes =
                notificationOutboxRepository.findAllByIsPublishedAndIsFailedAndIsPublishing(
                        false, false, false
                );

        outboxes.forEach(outbox -> outbox.setPublishing(true));
        return outboxes;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void releasePublishing(UUID outboxId) {
        NotificationOutbox outbox = findOutbox(outboxId);
        outbox.setPublishing(false);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markPublishing(UUID outboxId) {
        NotificationOutbox outbox = findOutbox(outboxId);
        outbox.setPublishing(true);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markPublished(UUID outboxId) {
        NotificationOutbox outbox = findOutbox(outboxId);
        outbox.setPublished(true);
        outbox.setPublishing(false);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markPublishAttemptFailed(UUID outboxId) {
        NotificationOutbox outbox = findOutbox(outboxId);
        int publishTries = outbox.getPublishTries() + 1;
        outbox.setPublishTries(publishTries);
        outbox.setPublishing(false);
        if (publishTries >= MAX_TRIES) {
            outbox.setFailed(true);
        }
    }

    private NotificationOutbox findOutbox(UUID outboxId) {
        return notificationOutboxRepository.findById(outboxId)
                .orElseThrow(() ->
                        new IllegalStateException("No notification outbox with id " + outboxId));
    }
}
