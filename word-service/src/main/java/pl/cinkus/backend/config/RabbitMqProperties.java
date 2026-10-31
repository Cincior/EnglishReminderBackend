package pl.cinkus.backend.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.rabbitmq")
@Data
@NoArgsConstructor
public class RabbitMqProperties {
    private String mobileQueue;
    private String exchange;
    private String mobileRoutingKey;
}
