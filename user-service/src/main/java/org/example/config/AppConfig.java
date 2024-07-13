package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
    public DaprClient daprClient() {
      return new DaprClientBuilder().build();
   }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
