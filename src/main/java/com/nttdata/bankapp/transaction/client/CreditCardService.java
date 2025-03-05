package com.nttdata.bankapp.transaction.client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@Slf4j
public class CreditCardService {

    private final WebClient webClient;

    public CreditCardService(@Value("${app.credit-card-service-url}") String creditCardServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(creditCardServiceUrl)
                .build();
    }

    public Mono<Boolean> creditCardExists(String creditCardId) {
        log.info("Checking if credit card exists with id: {}", creditCardId);
        return webClient.get()
                .uri("/credit-cards/{id}", creditCardId)
                .retrieve()
                .bodyToMono(Object.class)
                .map(response -> true)
                .onErrorResume(e -> {
                    log.error("Error checking credit card existence: {}", e.getMessage());
                    return Mono.just(false);
                });
    }

    /**
     * Registra un consumo en una tarjeta de crédito.
     * @param creditCardId ID de la tarjeta de crédito
     * @param amount Monto del consumo
     * @return Mono<CreditCardDto>
     */
    public Mono<CreditCardDto> registerConsumption(String creditCardId, BigDecimal amount) {
        log.info("Registering consumption for credit card id: {} with amount: {}", creditCardId, amount);
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/credit-cards/{id}/consumption")
                        .queryParam("amount", amount)
                        .build(creditCardId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CreditCardDto.class)
                .onErrorResume(e -> {
                    log.error("Error registering credit card consumption: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Error registering credit card consumption: " + e.getMessage()));
                });
    }

    /**
     * Obtiene el saldo disponible de una tarjeta de crédito.
     * @param creditCardId ID de la tarjeta de crédito
     * @return Mono<CreditCardBalanceDto>
     */
    public Mono<CreditCardBalanceDto> getBalance(String creditCardId) {
        log.info("Getting balance for credit card id: {}", creditCardId);
        return webClient.get()
                .uri("/credit-cards/{id}/balance", creditCardId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CreditCardBalanceDto.class)
                .onErrorResume(e -> {
                    log.error("Error getting credit card balance: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Error getting credit card balance: " + e.getMessage()));
                });
    }

    // DTOs internos para mapear las respuestas del servicio de tarjetas de crédito
    @lombok.Data
    public static class CreditCardDto {
        private String id;
        private String cardNumber;
        private String customerId;
        private BigDecimal creditLimit;
        private BigDecimal availableBalance;
    }

    @lombok.Data
    public static class CreditCardBalanceDto {
        private String creditCardId;
        private String cardNumber;
        private BigDecimal creditLimit;
        private BigDecimal availableBalance;
        private BigDecimal usedBalance;
    }
}
