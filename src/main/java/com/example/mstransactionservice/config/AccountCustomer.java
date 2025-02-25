package com.example.mstransactionservice.config;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AccountCustomer {
    private final WebClient webClient;

    public AccountCustomer(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://ms-account-service").build();
    }

    public Mono<Boolean> accountExists(String accountId) {
        return webClient.get()
                .uri("/api/accounts/{accountId}/exists", accountId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }

    public Mono<String> getAccountOwnerId(String accountId) {
        return webClient.get()
                .uri("/api/accounts/{accountId}/owner", accountId)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("");
    }
}
