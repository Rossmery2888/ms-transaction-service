package com.nttdata.bankapp.transaction.service.impl;


import com.nttdata.bankapp.transaction.client.AccountService;
import com.nttdata.bankapp.transaction.client.CreditCardService;
import com.nttdata.bankapp.transaction.client.CreditService;
import com.nttdata.bankapp.transaction.client.CustomerService;
import com.nttdata.bankapp.transaction.dto.*;
import com.nttdata.bankapp.transaction.exception.ResourceNotFoundException;
import com.nttdata.bankapp.transaction.model.Transaction;
import com.nttdata.bankapp.transaction.model.TransactionType;
import com.nttdata.bankapp.transaction.repository.TransactionRepository;
import com.nttdata.bankapp.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
/**
 * Implementación de los servicios para operaciones con transacciones.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CreditService creditService;
    private final CreditCardService creditCardService;
    private final CustomerService customerService;

    @Override
    public Flux<TransactionDto> findAll() {
        log.info("Finding all transactions");
        return transactionRepository.findAll()
                .map(this::mapToDto);
    }

    @Override
    public Mono<TransactionDto> findById(String id) {
        log.info("Finding transaction by id: {}", id);
        return transactionRepository.findById(id)
                .map(this::mapToDto)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Transaction not found with id: " + id)));
    }

    @Override
    public Flux<TransactionDto> findByAccountId(String accountId) {
        log.info("Finding transactions by account id: {}", accountId);
        return transactionRepository.findByAccountId(accountId)
                .map(this::mapToDto);
    }

    @Override
    public Flux<TransactionDto> findByCreditId(String creditId) {
        log.info("Finding transactions by credit id: {}", creditId);
        return transactionRepository.findByCreditId(creditId)
                .map(this::mapToDto);
    }

    @Override
    public Flux<TransactionDto> findByCreditCardId(String creditCardId) {
        log.info("Finding transactions by credit card id: {}", creditCardId);
        return transactionRepository.findByCreditCardId(creditCardId)
                .map(this::mapToDto);
    }

    @Override
    public Flux<TransactionDto> findByCustomerId(String customerId) {
        log.info("Finding transactions by customer id: {}", customerId);
        return transactionRepository.findByCustomerId(customerId)
                .map(this::mapToDto);
    }

    @Override
    public Flux<TransactionDto> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Finding transactions between {} and {}", startDate, endDate);
        return transactionRepository.findByTransactionDateBetween(startDate, endDate)
                .map(this::mapToDto);
    }

    @Override
    public Flux<TransactionDto> findByAccountIdAndDateRange(String accountId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Finding transactions for account {} between {} and {}", accountId, startDate, endDate);
        return transactionRepository.findByAccountIdAndTransactionDateBetween(accountId, startDate, endDate)
                .map(this::mapToDto);
    }

    @Override
    public Mono<TransactionDto> deposit(DepositRequest request) {
        log.info("Processing deposit request: {}", request);

        // Verificar si el cliente existe
        return customerService.customerExists(request.getCustomerId())
                .flatMap(customerExists -> {
                    if (!customerExists) {
                        return Mono.error(new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
                    }

                    // Verificar si la cuenta existe
                    return accountService.accountExists(request.getAccountId())
                            .flatMap(accountExists -> {
                                if (!accountExists) {
                                    return Mono.error(new ResourceNotFoundException("Account not found with id: " + request.getAccountId()));
                                }

                                // Crear la transacción
                                Transaction transaction = Transaction.builder()
                                        .accountId(request.getAccountId())
                                        .type(TransactionType.DEPOSIT)
                                        .amount(request.getAmount())
                                        .description(request.getDescription())
                                        .transactionDate(LocalDateTime.now())
                                        .customerId(request.getCustomerId())
                                        .referenceNumber(generateReferenceNumber())
                                        .build();

                                // Actualizar el saldo de la cuenta
                                return accountService.updateBalance(request.getAccountId(), request.getAmount())
                                        .flatMap(account -> transactionRepository.save(transaction))
                                        .map(this::mapToDto);
                            });
                });
    }

    @Override
    public Mono<TransactionDto> withdraw(WithdrawalRequest request) {
        log.info("Processing withdrawal request: {}", request);

        // Verificar si el cliente existe
        return customerService.customerExists(request.getCustomerId())
                .flatMap(customerExists -> {
                    if (!customerExists) {
                        return Mono.error(new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
                    }

                    // Verificar si la cuenta existe
                    return accountService.accountExists(request.getAccountId())
                            .flatMap(accountExists -> {
                                if (!accountExists) {
                                    return Mono.error(new ResourceNotFoundException("Account not found with id: " + request.getAccountId()));
                                }

                                // Verificar si hay saldo suficiente
                                return accountService.getBalance(request.getAccountId())
                                        .flatMap(balance -> {
                                            if (balance.getBalance().compareTo(request.getAmount()) < 0) {
                                                return Mono.error(new IllegalArgumentException("Insufficient funds"));
                                            }

                                            // Crear la transacción
                                            Transaction transaction = Transaction.builder()
                                                    .accountId(request.getAccountId())
                                                    .type(TransactionType.WITHDRAWAL)
                                                    .amount(request.getAmount())
                                                    .description(request.getDescription())
                                                    .transactionDate(LocalDateTime.now())
                                                    .customerId(request.getCustomerId())
                                                    .referenceNumber(generateReferenceNumber())
                                                    .build();

                                            // Actualizar el saldo de la cuenta (monto negativo para retiro)
                                            return accountService.updateBalance(request.getAccountId(), request.getAmount().negate())
                                                    .flatMap(account -> transactionRepository.save(transaction))
                                                    .map(this::mapToDto);
                                        });
                            });
                });
    }

    @Override
    public Mono<TransactionDto> payCredit(PaymentRequest request) {
        log.info("Processing credit payment request: {}", request);

        // Verificar si el cliente existe
        return customerService.customerExists(request.getCustomerId())
                .flatMap(customerExists -> {
                    if (!customerExists) {
                        return Mono.error(new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
                    }

                    // Verificar si el crédito existe
                    return creditService.creditExists(request.getCreditId())
                            .flatMap(creditExists -> {
                                if (!creditExists) {
                                    return Mono.error(new ResourceNotFoundException("Credit not found with id: " + request.getCreditId()));
                                }

                                // Crear la transacción
                                Transaction transaction = Transaction.builder()
                                        .creditId(request.getCreditId())
                                        .type(TransactionType.PAYMENT)
                                        .amount(request.getAmount())
                                        .description(request.getDescription())
                                        .transactionDate(LocalDateTime.now())
                                        .customerId(request.getCustomerId())
                                        .referenceNumber(generateReferenceNumber())
                                        .build();

                                // Realizar el pago al crédito
                                return creditService.makePayment(request.getCreditId(), request.getAmount())
                                        .flatMap(credit -> transactionRepository.save(transaction))
                                        .map(this::mapToDto);
                            });
                });
    }

    @Override
    public Mono<TransactionDto> consumeCreditCard(ConsumptionRequest request) {
        log.info("Processing credit card consumption request: {}", request);

        // Verificar si el cliente existe
        return customerService.customerExists(request.getCustomerId())
                .flatMap(customerExists -> {
                    if (!customerExists) {
                        return Mono.error(new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
                    }

                    // Verificar si la tarjeta de crédito existe
                    return creditCardService.creditCardExists(request.getCreditCardId())
                            .flatMap(cardExists -> {
                                if (!cardExists) {
                                    return Mono.error(new ResourceNotFoundException("Credit card not found with id: " + request.getCreditCardId()));
                                }

                                // Verificar si hay límite disponible
                                return creditCardService.getBalance(request.getCreditCardId())
                                        .flatMap(balance -> {
                                            if (balance.getAvailableBalance().compareTo(request.getAmount()) < 0) {
                                                return Mono.error(new IllegalArgumentException("Insufficient credit limit"));
                                            }

                                            // Crear la transacción
                                            Transaction transaction = Transaction.builder()
                                                    .creditCardId(request.getCreditCardId())
                                                    .type(TransactionType.CONSUMPTION)
                                                    .amount(request.getAmount())
                                                    .description(request.getDescription())
                                                    .transactionDate(LocalDateTime.now())
                                                    .customerId(request.getCustomerId())
                                                    .referenceNumber(generateReferenceNumber())
                                                    .build();

                                            // Registrar el consumo en la tarjeta
                                            return creditCardService.registerConsumption(request.getCreditCardId(), request.getAmount())
                                                    .flatMap(card -> transactionRepository.save(transaction))
                                                    .map(this::mapToDto);
                                        });
                            });
                });
    }
    /**
     * Convierte una entidad Transaction a DTO.
     * @param transaction Entidad a convertir
     * @return TransactionDto
     */
    private TransactionDto mapToDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccountId())
                .creditId(transaction.getCreditId())
                .creditCardId(transaction.getCreditCardId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .transactionDate(transaction.getTransactionDate())
                .customerId(transaction.getCustomerId())
                .referenceNumber(transaction.getReferenceNumber())
                .build();
    }
    @Override
    public Mono<TransactionDto> transfer(TransferRequest request) {
        log.info("Processing transfer request: {}", request);

        // Verificar si el cliente existe
        return customerService.customerExists(request.getCustomerId())
                .flatMap(customerExists -> {
                    if (!customerExists) {
                        return Mono.error(new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
                    }

                    // Validar que la cuenta origen pertenezca al cliente y tenga fondos suficientes
                    return accountService.validateAccountForTransfer(
                            request.getSourceAccountId(),
                            request.getCustomerId(),
                            request.getAmount()
                    );
                })
                .flatMap(valid -> {
                    // Verificar si la cuenta destino existe
                    return accountService.accountExists(request.getDestinationAccountId())
                            .flatMap(exists -> {
                                if (!exists) {
                                    return Mono.error(new ResourceNotFoundException("Destination account not found with id: " + request.getDestinationAccountId()));
                                }

                                // Calcular comisión por transferencia (si aplica)
                                return accountService.calculateTransactionFee(request.getSourceAccountId())
                                        .flatMap(fee -> {
                                            // Crear la transacción
                                            Transaction transaction = Transaction.builder()
                                                    .accountId(request.getSourceAccountId())
                                                    .destinationAccountId(request.getDestinationAccountId())
                                                    .type(TransactionType.TRANSFER)
                                                    .amount(request.getAmount())
                                                    .fee(fee)
                                                    .description(request.getDescription())
                                                    .transactionDate(LocalDateTime.now())
                                                    .customerId(request.getCustomerId())
                                                    .referenceNumber(generateReferenceNumber())
                                                    .build();

                                            // Actualizar saldos y contadores de transacciones en ambas cuentas
                                            return Mono.zip(
                                                    // Decrementar saldo en cuenta origen
                                                    accountService.updateBalance(
                                                            request.getSourceAccountId(),
                                                            request.getAmount().negate()
                                                    ),
                                                    // Incrementar saldo en cuenta destino
                                                    accountService.updateBalance(
                                                            request.getDestinationAccountId(),
                                                            request.getAmount()
                                                    )
                                            ).flatMap(tuple -> {
                                                // Si hay comisión, actualizar saldo y contador de la cuenta origen
                                                if (fee.compareTo(BigDecimal.ZERO) > 0) {
                                                    return accountService.incrementTransactionCount(request.getSourceAccountId(), fee)
                                                            .flatMap(account -> transactionRepository.save(transaction));
                                                } else {
                                                    // Si no hay comisión, solo actualizar contador
                                                    return accountService.incrementTransactionCount(request.getSourceAccountId(), null)
                                                            .flatMap(account -> transactionRepository.save(transaction));
                                                }
                                            });
                                        });
                            });
                })
                .map(this::mapToDto);
    }

    // Método utilitario para generar número de referencia
    private String generateReferenceNumber() {
        return "TX-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }
}

