package si.puzzle.cobiss.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(CobissProperties.class)
public class AppConfig {

    @Bean
    RestClient cobissRestClient(CobissProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getApiBaseUrl())
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
