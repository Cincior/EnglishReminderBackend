package pl.cinkus.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cinkus.backend.client.RabbitMqMessageSender;
import pl.cinkus.backend.entity.Notification;
import pl.cinkus.backend.entity.NotificationOutbox;
import pl.cinkus.backend.repository.NotificationRepository;
import pl.cinkus.messages.SendMobileNotificationMessage;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final RabbitMqMessageSender rabbitMqMessageSender;
    private final NotificationOutboxService notificationOutboxService;

    public void sendPendingNotifications() {
        // We retry to send a notification every time scheduler fires because we want to make sure
        // that user gets their notification on time or at least with the minimum delay possible
        List<NotificationOutbox> notificationOutboxes =
                notificationOutboxService.claimPendingNotifications();
        if (notificationOutboxes.isEmpty()) {
            return;
        }
        log.info("outboxes:" + notificationOutboxes);

        for (NotificationOutbox notificationOutbox : notificationOutboxes) {
            Notification n = notificationOutbox.getNotification();
            if (n == null) {
                log.warn("Notification with given id could not be found and could not be sent to rabbitmq");
                notificationOutboxService.releasePublishing(notificationOutbox.getId());
                continue;
            }
            if (Instant.parse(n.getDateTime()).isBefore(Instant.now().minus(2, ChronoUnit.SECONDS /* To give some time for handling */))) {
                notificationOutboxService.releasePublishing(notificationOutbox.getId());
                continue;
            }
            if (notificationOutbox.getPublishTries() >= 5) {
                notificationOutboxService.markPublishAttemptFailed(notificationOutbox.getId());
                continue;
            }
            log.info("not:" + n);
            rabbitMqMessageSender.sendMessage(SendMobileNotificationMessage.builder()
                            .outboxId(notificationOutbox.getId())
                            .fcmToken(n.getFcmToken())
                            .word(n.getWord())
                            .translation(n.getTranslation())
                            .dateTime(n.getDateTime())
                            .build())
                    .whenComplete((published, exception) -> {
                        if (exception != null) {
                            notificationOutboxService.markPublishAttemptFailed(notificationOutbox.getId());
                            return;
                        }

                        notificationOutboxService.markPublished(notificationOutbox.getId());
                    });
        }
    }
}
