package pl.cinkus.notification_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import pl.cinkus.messages.RegistrationNotificationMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqConsumer {
    private final JavaMailSender mailSender;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void consumeMessage(RegistrationNotificationMessage message) {
        SimpleMailMessage message1 = new SimpleMailMessage();

        message1.setFrom("englishreminderappnotification@gmail.com");
        message1.setTo(message.getEmail());
        message1.setSubject("Twoj kod weryfikacyjny!");
        message1.setText("Twoj kod do weryfikacji konta: " + message.getCode());

        mailSender.send(message1);

        System.out.println("Otrzymano nową wiadomość z Identity Service: " + message);
        log.info("XDDDD" + message);
    }
}
