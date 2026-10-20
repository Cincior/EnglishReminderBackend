package pl.cinkus.notification_service.config;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.rabbitmq")
@Data
@NoArgsConstructor
public class RabbitMqProperties {

    private String queue;
    private String exchange;
    private String routingKey;
}
