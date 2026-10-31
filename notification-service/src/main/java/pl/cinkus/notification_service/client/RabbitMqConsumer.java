package pl.cinkus.notification_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.cinkus.messages.SendEmailWithCodeMessage;
import pl.cinkus.messages.SendMobileNotificationMessage;
import pl.cinkus.notification_service.service.EmailService;
import pl.cinkus.notification_service.service.MobileNotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqConsumer {
    private final EmailService emailService;
    private final MobileNotificationService mobileNotificationService;

    @RabbitListener(queues = "${app.rabbitmq.emailQueue}")
    public void consumeMessage(SendEmailWithCodeMessage message) {
        emailService.sendEmail(message);
    }

    @RabbitListener(queues = "${app.rabbitmq.mobileQueue}")
    public void consumeMessage(SendMobileNotificationMessage message) {
        log.info("Tutaj mobile: " + message);
    }
}
