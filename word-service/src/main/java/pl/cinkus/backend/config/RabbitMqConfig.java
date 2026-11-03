package pl.cinkus.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {
    private final RabbitMqProperties rabbitMqProperties;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(rabbitMqProperties.getExchange());
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);

        rabbitTemplate.setBeforePublishPostProcessors(message -> {

            message.getMessageProperties().setDeliveryMode(
                    MessageDeliveryMode.PERSISTENT
            );

            return message;
        });

        return rabbitTemplate;
    }
}
