    package pl.cinkus.backend.config;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.client.JdkClientHttpRequestFactory;
    import org.springframework.web.client.RestClient;

    import java.net.http.HttpClient;

    @Configuration
    public class AppConfig {

        @Bean
        public RestClient predictApiClient() {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();

            return RestClient.builder()
                    .baseUrl("http://predict-service:8000")
                    .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                    .build();
        }
    }
