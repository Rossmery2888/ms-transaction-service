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
public class AccountService {

    private final WebClient webClient;

    public AccountService(@Value("${app.account-service-url}") String accountServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(accountServiceUrl)
                .build();
    }

    /**
     * Actualiza el saldo de una cuenta.
     * @param accountId ID de la cuenta
     * @param amount Monto a actualizar (positivo para depósitos, negativo para retiros)
     * @return Mono<AccountDto>
     */
    public Mono<AccountDto> updateBalance(String accountId, BigDecimal amount) {
        log.info("Updating balance for account id: {} with amount: {}", accountId, amount);
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{id}/balance")
                        .queryParam("amount", amount)
                        .build(accountId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AccountDto.class)
                .onErrorResume(e -> {
                    log.error("Error updating account balance: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Error updating account balance: " + e.getMessage()));
                });
    }

    /**
     * Obtiene el saldo de una cuenta.
     * @param accountId ID de la cuenta
     * @return Mono<BalanceDto>
     */
    public Mono<BalanceDto> getBalance(String accountId) {
        log.info("Getting balance for account id: {}", accountId);
        return webClient.get()
                .uri("/accounts/{id}/balance", accountId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BalanceDto.class)
                .onErrorResume(e -> {
                    log.error("Error getting account balance: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Error getting account balance: " + e.getMessage()));
                });
    }
    /**
     * Verifica si una cuenta existe.
     * @param accountId ID de la cuenta
     * @return Mono<Boolean> true si existe, false en caso contrario
     */
    public Mono<Boolean> accountExists(String accountId) {
        log.info("Checking if account exists with id: {}", accountId);
        return webClient.get()
                .uri("/accounts/{id}", accountId)
                .retrieve()
                .bodyToMono(Object.class)
                .map(response -> true)
                .onErrorResume(e -> {
                    log.error("Error checking account existence: {}", e.getMessage());
                    return Mono.just(false);
                });
    }

    /**
     * Valida una cuenta para realizar transferencia.
     * @param accountId ID de la cuenta
     * @param customerId ID del cliente
     * @param amount Monto a transferir
     * @return Mono<Boolean> true si es válida
     */
    public Mono<Boolean> validateAccountForTransfer(String accountId, String customerId, BigDecimal amount) {
        log.info("Validating account for transfer: account={}, customer={}, amount={}", accountId, customerId, amount);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{id}/validate-transfer")
                        .queryParam("customerId", customerId)
                        .queryParam("amount", amount)
                        .build(accountId))
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(e -> {
                    log.error("Error validating account for transfer: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Invalid account for transfer: " + e.getMessage()));
                });
    }

    /**
     * Calcula la comisión por transacción para una cuenta.
     * @param accountId ID de la cuenta
     * @return Mono<BigDecimal> con el monto de la comisión
     */
    public Mono<BigDecimal> calculateTransactionFee(String accountId) {
        log.info("Calculating transaction fee for account: {}", accountId);
        return webClient.get()
                .uri("/accounts/{id}/transaction-fee", accountId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BigDecimal.class)
                .onErrorResume(e -> {
                    log.error("Error calculating transaction fee: {}", e.getMessage());
                    return Mono.just(BigDecimal.ZERO);
                });
    }

    /**
     * Incrementa el contador de transacciones de una cuenta y aplica comisión si es necesario.
     * @param accountId ID de la cuenta
     * @param fee Comisión a aplicar (puede ser null)
     * @return Mono<AccountDto>
     */
    public Mono<AccountDto> incrementTransactionCount(String accountId, BigDecimal fee) {
        log.info("Incrementing transaction count for account: {}, fee: {}", accountId, fee);
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{id}/transaction-count")
                        .queryParam("fee", fee != null ? fee : 0)
                        .build(accountId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AccountDto.class)
                .onErrorResume(e -> {
                    log.error("Error incrementing transaction count: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Error incrementing transaction count: " + e.getMessage()));
                });
    }


    // DTOs internos para mapear las respuestas del servicio de cuentas
    @lombok.Data
    public static class AccountDto {
        private String id;
        private String accountNumber;
        private String type;
        private String customerId;
        private BigDecimal balance;
    }

    @lombok.Data
    public static class BalanceDto {
        private String accountId;
        private String accountNumber;
        private String accountType;
        private BigDecimal balance;
        private Integer remainingMonthlyMovements;
    }
}