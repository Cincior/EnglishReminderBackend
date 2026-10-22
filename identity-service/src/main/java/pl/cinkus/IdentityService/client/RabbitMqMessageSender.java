package pl.cinkus.IdentityService.client;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.cinkus.IdentityService.config.RabbitMqProperties;
import pl.cinkus.messages.SendEmailWithCodeMessage;

@Component
@RequiredArgsConstructor
public class RabbitMqMessageSender {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties properites;

    public void sendMessage(SendEmailWithCodeMessage message) {
        rabbitTemplate.convertAndSend(properites.getExchange(), properites.getRoutingKey(), message);
    }
}
