package pl.cinkus.backend.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.cinkus.backend.service.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private final NotificationService notificationService;

    @Scheduled(cron = "*/15 * * * * *")
    public void handleSendingNotifications() {
        log.info("Schedule co x sekund");
        notificationService.sendPendingNotifications();
    }
}
