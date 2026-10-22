package pl.cinkus.notification_service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.cinkus.messages.SendEmailWithCodeMessage;
import pl.cinkus.notification_service.service.EmailService;

@Component
@RequiredArgsConstructor
public class RabbitMqConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void consumeMessage(SendEmailWithCodeMessage message) {
        emailService.sendEmail(message);
    }
}
