package com.ritvik.zodiacverseBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // RestTemplate for calling OpenAI API
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}