package com.example.imageprotection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

/**
 * Provides Spring beans for general application configuration.
 */
@Configuration
public class AppConfig {
    /**
     * REST client bean for making HTTP requests (e.g., to Azure OCR).
     * @param builder RestTemplateBuilder auto-configured by Spring.
     * @return RestTemplate instance for HTTP calls.
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
