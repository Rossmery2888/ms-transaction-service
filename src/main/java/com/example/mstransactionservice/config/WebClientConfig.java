package com.example.mstransactionservice.config;

import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient accountServiceWebClient() {
        return WebClient.create("http://ms-account-service-url");
    }
}