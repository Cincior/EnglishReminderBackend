package pl.cinkus.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Bean
    public RestClient PredictApiClient() {
        return RestClient.builder()
                .baseUrl("http://predict-service:8000")
                .build();
    }
}
