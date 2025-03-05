package com.nttdata.bankapp.transaction.controller;

import com.nttdata.bankapp.transaction.dto.*;
import com.nttdata.bankapp.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * Controlador para operaciones con transacciones.
 */
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Obtiene todas las transacciones.
     * @return Flux de TransactionDto
     */
    @GetMapping
    public Flux<TransactionDto> getAll() {
        log.info("GET /transactions");
        return transactionService.findAll();
    }

    /**
     * Obtiene una transacción por su ID.
     * @param id ID de la transacción
     * @return Mono de TransactionDto
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TransactionDto>> getById(@PathVariable String id) {
        log.info("GET /transactions/{}", id);
        return transactionService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene las transacciones de una cuenta.
     * @param accountId ID de la cuenta
     * @return Flux de TransactionDto
     */
    @GetMapping("/account/{accountId}")
    public Flux<TransactionDto> getByAccountId(@PathVariable String accountId) {
        log.info("GET /transactions/account/{}", accountId);
        return transactionService.findByAccountId(accountId);
    }

    /**
     * Obtiene las transacciones de un crédito.
     * @param creditId ID del crédito
     * @return Flux de TransactionDto
     */
    @GetMapping("/credit/{creditId}")
    public Flux<TransactionDto> getByCreditId(@PathVariable String creditId) {
        log.info("GET /transactions/credit/{}", creditId);
        return transactionService.findByCreditId(creditId);
    }

    /**
     * Obtiene las transacciones de una tarjeta de crédito.
     * @param creditCardId ID de la tarjeta de crédito
     * @return Flux de TransactionDto
     */
    @GetMapping("/credit-card/{creditCardId}")
    public Flux<TransactionDto> getByCreditCardId(@PathVariable String creditCardId) {
        log.info("GET /transactions/credit-card/{}", creditCardId);
        return transactionService.findByCreditCardId(creditCardId);
    }

    /**
     * Obtiene las transacciones de un cliente.
     * @param customerId ID del cliente
     * @return Flux de TransactionDto
     */
    @GetMapping("/customer/{customerId}")
    public Flux<TransactionDto> getByCustomerId(@PathVariable String customerId) {
        log.info("GET /transactions/customer/{}", customerId);
        return transactionService.findByCustomerId(customerId);
    }

    /**
     * Obtiene las transacciones en un rango de fechas.
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Flux de TransactionDto
     */
    @GetMapping("/date-range")
    public Flux<TransactionDto> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /transactions/date-range between {} and {}", startDate, endDate);
        return transactionService.findByDateRange(startDate, endDate);
    }

    /**
     * Obtiene las transacciones de una cuenta en un rango de fechas.
     * @param accountId ID de la cuenta
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Flux de TransactionDto
     */
    @GetMapping("/account/{accountId}/date-range")
    public Flux<TransactionDto> getByAccountIdAndDateRange(
            @PathVariable String accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /transactions/account/{}/date-range between {} and {}", accountId, startDate, endDate);
        return transactionService.findByAccountIdAndDateRange(accountId, startDate, endDate);
    }

    /**
     * Realiza un depósito en una cuenta.
     * @param request DTO con los datos del depósito
     * @return Mono de TransactionDto
     */
    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransactionDto> deposit(@Valid @RequestBody DepositRequest request) {
        log.info("POST /transactions/deposit with request: {}", request);
        return transactionService.deposit(request);
    }

    /**
     * Realiza un retiro de una cuenta.
     * @param request DTO con los datos del retiro
     * @return Mono de TransactionDto
     */
    @PostMapping("/withdrawal")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransactionDto> withdraw(@Valid @RequestBody WithdrawalRequest request) {
        log.info("POST /transactions/withdrawal with request: {}", request);
        return transactionService.withdraw(request);
    }

    /**
     * Realiza un pago a un crédito.
     * @param request DTO con los datos del pago
     * @return Mono de TransactionDto
     */
    @PostMapping("/payment")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransactionDto> payCredit(@Valid @RequestBody PaymentRequest request) {
        log.info("POST /transactions/payment with request: {}", request);
        return transactionService.payCredit(request);
    }

    /**
     * Registra un consumo de tarjeta de crédito.
     * @param request DTO con los datos del consumo
     * @return Mono de TransactionDto
     */
    @PostMapping("/consumption")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransactionDto> consumeCreditCard(@Valid @RequestBody ConsumptionRequest request) {
        log.info("POST /transactions/consumption with request: {}", request);
        return transactionService.consumeCreditCard(request);
    }
    /**
     * Realiza una transferencia entre cuentas.
     * @param request DTO con los datos de la transferencia
     * @return Mono de TransactionDto
     */
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransactionDto> transfer(@Valid @RequestBody TransferRequest request) {
        log.info("POST /transactions/transfer with request: {}", request);
        return transactionService.transfer(request);
    }
}