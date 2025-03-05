package com.nttdata.bankapp.transaction.service;

import com.nttdata.bankapp.transaction.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Interfaz que define los servicios para operaciones con transacciones.
 */
public interface TransactionService {
    Flux<TransactionDto> findAll();
    Mono<TransactionDto> findById(String id);
    Flux<TransactionDto> findByAccountId(String accountId);
    Flux<TransactionDto> findByCreditId(String creditId);
    Flux<TransactionDto> findByCreditCardId(String creditCardId);
    Flux<TransactionDto> findByCustomerId(String customerId);
    Flux<TransactionDto> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    Flux<TransactionDto> findByAccountIdAndDateRange(String accountId, LocalDateTime startDate, LocalDateTime endDate);
    Mono<TransactionDto> deposit(DepositRequest request);
    Mono<TransactionDto> withdraw(WithdrawalRequest request);
    Mono<TransactionDto> payCredit(PaymentRequest request);
    Mono<TransactionDto> consumeCreditCard(ConsumptionRequest request);
    /**
     * Realiza una transferencia entre cuentas.
     * @param request Datos de la transferencia
     * @return TransactionDto con los datos de la transacción
     */
    Mono<TransactionDto> transfer(TransferRequest request);
}
