package pl.cinkus.backend.client;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.cinkus.backend.config.RabbitMqProperties;
import pl.cinkus.messages.SendMobileNotificationMessage;

@Component
@RequiredArgsConstructor
public class RabbitMqMessageSender {
    private final RabbitMqProperties rabbitMqConfig;
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(SendMobileNotificationMessage mobileNotificationMessage) {
        rabbitTemplate.convertAndSend(rabbitMqConfig.getExchange(), rabbitMqConfig.getMobileRoutingKey(), mobileNotificationMessage);
    }
}
