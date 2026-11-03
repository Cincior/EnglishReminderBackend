package pl.cinkus.backend.client;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.cinkus.backend.config.RabbitMqProperties;
import pl.cinkus.messages.SendMobileNotificationMessage;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class RabbitMqMessageSender {
    private final RabbitMqProperties rabbitMqConfig;
    private final RabbitTemplate rabbitTemplate;

    public CompletableFuture<Boolean> sendMessage(SendMobileNotificationMessage mobileNotificationMessage) {
        CorrelationData correlationData =
                new CorrelationData(UUID.randomUUID().toString());

        CompletableFuture<Boolean> result = new CompletableFuture<>();

        correlationData.getFuture().whenComplete(((confirm, error) -> {
            if (error != null) {
                result.completeExceptionally(error);
                return;
            }

            if (!confirm.isAck()) {
                result.completeExceptionally(
                        new IllegalStateException(
                                "RabbitMQ rejected message: " + confirm.getReason()
                        )
                );
                return;
            }

            result.complete(true);
        }));

        try {
            rabbitTemplate.convertAndSend(
                    rabbitMqConfig.getExchange(),
                    rabbitMqConfig.getMobileRoutingKey(),
                    mobileNotificationMessage,
                    correlationData
            );
        } catch (RuntimeException exception) {
            result.completeExceptionally(exception);
        }

        return result;
    }
}
