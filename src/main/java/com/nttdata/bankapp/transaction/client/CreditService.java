package com.nttdata.bankapp.transaction.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Cliente para comunicarse con el microservicio de créditos.
 */
@Service
@Slf4j
public class CreditService {

    private final WebClient webClient;

    public CreditService(@Value("${app.credit-service-url}") String creditServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(creditServiceUrl)
                .build();
    }

    /**
     * Verifica si un crédito existe por su ID.
     * @param creditId ID del crédito
     * @return Mono<Boolean> true si existe, false en caso contrario
     */
    public Mono<Boolean> creditExists(String creditId) {
        log.info("Checking if credit exists with id: {}", creditId);
        return webClient.get()
                .uri("/credits/{id}", creditId)
                .retrieve()
                .bodyToMono(Object.class)
                .map(response -> true)
                .onErrorResume(e -> {
                    log.error("Error checking credit existence: {}", e.getMessage());
                    return Mono.just(false);
                });
    }

    /**
     * Realiza un pago a un crédito.
     * @param creditId ID del crédito
     * @param amount Monto a pagar
     * @return Mono<CreditDto>
     */
    public Mono<CreditDto> makePayment(String creditId, BigDecimal amount) {
        log.info("Making payment to credit id: {} with amount: {}", creditId, amount);
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/credits/{id}/payment")
                        .queryParam("amount", amount)
                        .build(creditId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CreditDto.class)
                .onErrorResume(e -> {
                    log.error("Error making credit payment: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Error making credit payment: " + e.getMessage()));
                });
    }

    /**
     * Obtiene el saldo de un crédito.
     * @param creditId ID del crédito
     * @return Mono<CreditBalanceDto>
     */
    public Mono<CreditBalanceDto> getBalance(String creditId) {
        log.info("Getting balance for credit id: {}", creditId);
        return webClient.get()
                .uri("/credits/{id}/balance", creditId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CreditBalanceDto.class)
                .onErrorResume(e -> {
                    log.error("Error getting credit balance: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Error getting credit balance: " + e.getMessage()));
                });
    }

    // DTOs internos para mapear las respuestas del servicio de créditos
    @lombok.Data
    public static class CreditDto {
        private String id;
        private String creditNumber;
        private String customerId;
        private BigDecimal amount;
        private BigDecimal remainingAmount;
    }

    @lombok.Data
    public static class CreditBalanceDto {
        private String creditId;
        private String creditNumber;
        private BigDecimal totalAmount;
        private BigDecimal remainingAmount;
        private BigDecimal paidAmount;
    }
}
