package com.example.mstransactionservice.service.Impl;


import com.example.mstransactionservice.Exception.BusinessException;
import com.example.mstransactionservice.config.AccountCustomer;
import com.example.mstransactionservice.config.TransactionConfig;
import com.example.mstransactionservice.dto.ThirdPartyPaymentRequest;
import com.example.mstransactionservice.model.Transaction;
import com.example.mstransactionservice.dto.TransactionRequest;
import com.example.mstransactionservice.dto.TransferRequest;
import com.example.mstransactionservice.model.enums.TransactionType;
import com.example.mstransactionservice.repository.TransactionRepository;
import com.example.mstransactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionConfig transactionConfig;
    private final AccountCustomer accountCustomer;

    @Override
    public Mono<Transaction> registerTransaction(TransactionRequest request) {
        return getTransactionCountForMonth(request.getAccountId(), YearMonth.now())
                .flatMap(count -> {
                    // Verificar si se excedió el límite de transacciones gratuitas
                    BigDecimal finalAmount = request.getAmount();
                    if (count >= transactionConfig.getFreeTransactionLimit()) {
                        // Aplicar comisión
                        finalAmount = finalAmount.add(transactionConfig.getCommissionFee());
                    }

                    // Crear objeto Transaction y establecer sus propiedades
                    Transaction transaction = new Transaction();
                    transaction.setId(UUID.randomUUID().toString());
                    transaction.setAccountId(request.getAccountId());
                    transaction.setRelatedEntityId(request.getRelatedEntityId());
                    transaction.setType(request.getType());
                    transaction.setAmount(finalAmount);
                    transaction.setTimestamp(LocalDateTime.now());

                    return transactionRepository.save(transaction);
                });
    }

    @Override
    public Flux<Transaction> getTransactionsByAccountId(String accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    public Flux<Transaction> getTransactionsByRelatedEntityId(String relatedEntityId) {
        return transactionRepository.findByRelatedEntityId(relatedEntityId);
    }

    @Override
    public Mono<Integer> getTransactionCountForMonth(String accountId, YearMonth yearMonth) {
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay();

        return transactionRepository.findByAccountId(accountId)
                .filter(t -> t.getTimestamp().isAfter(startOfMonth) && t.getTimestamp().isBefore(endOfMonth))
                .count()
                .map(Long::intValue);
    }

    @Override
    public Mono<Transaction> transferInternal(TransferRequest request) {
        return validateAccounts(request)
                .flatMap(valid -> {
                    // Verificar que ambas cuentas sean del mismo cliente
                    return Mono.zip(
                            accountCustomer.getAccountOwnerId(request.getSourceAccountId()),
                            accountCustomer.getAccountOwnerId(request.getDestinationAccountId())
                    ).flatMap(tuple -> {
                        String sourceOwnerId = tuple.getT1();
                        String destOwnerId = tuple.getT2();

                        if (!sourceOwnerId.equals(destOwnerId)) {
                            return Mono.error(new BusinessException("Las cuentas deben pertenecer al mismo cliente para transferencias internas"));
                        }

                        // Registrar transacción de salida
                        TransactionRequest withdrawalRequest = new TransactionRequest(
                                request.getSourceAccountId(),
                                request.getDestinationAccountId(),
                                TransactionType.TRANSFER_INTERNAL,
                                request.getAmount().negate()
                        );

                        return registerTransaction(withdrawalRequest);
                    });
                });
    }

    @Override
    public Mono<Transaction> transferExternal(TransferRequest request) {
        return validateAccounts(request)
                .flatMap(valid -> {
                    // Registrar transacción de salida
                    TransactionRequest withdrawalRequest = new TransactionRequest(
                            request.getSourceAccountId(),
                            request.getDestinationAccountId(),
                            TransactionType.TRANSFER_EXTERNAL,
                            request.getAmount().negate()
                    );

                    return registerTransaction(withdrawalRequest);
                });
    }

    private Mono<Boolean> validateAccounts(TransferRequest request) {
        // Verificar que las cuentas existan
        return Mono.zip(
                accountCustomer.accountExists(request.getSourceAccountId()),
                accountCustomer.accountExists(request.getDestinationAccountId())
        ).flatMap(tuple -> {
            boolean sourceExists = tuple.getT1();
            boolean destExists = tuple.getT2();

            if (!sourceExists) {
                return Mono.error(new BusinessException("La cuenta de origen no existe"));
            }

            if (!destExists) {
                return Mono.error(new BusinessException("La cuenta de destino no existe"));
            }

            if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return Mono.error(new BusinessException("El monto debe ser mayor a cero"));
            }

            return Mono.just(true);
        });
    }
    // NUEVA FUNCIONALIDAD 1: Registrar pagos de terceros
    @Override
    public Mono<Transaction> registerThirdPartyPayment(ThirdPartyPaymentRequest request) {
        // Verificar que la cuenta existe
        return accountCustomer.accountExists(request.getAccountId())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new BusinessException("La cuenta no existe"));
                    }

                    if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                        return Mono.error(new BusinessException("El monto debe ser mayor a cero"));
                    }

                    return getTransactionCountForMonth(request.getAccountId(), YearMonth.now())
                            .flatMap(count -> {
                                // Crear objeto Transaction para el pago a terceros
                                Transaction transaction = new Transaction();
                                transaction.setId(UUID.randomUUID().toString());
                                transaction.setAccountId(request.getAccountId());
                                transaction.setRelatedEntityId(request.getCreditProductId());
                                transaction.setType(TransactionType.THIRD_PARTY_PAYMENT);

                                // Verificar si se excedió el límite de transacciones gratuitas
                                BigDecimal finalAmount = request.getAmount().negate(); // Negativo porque es un pago
                                if (count >= transactionConfig.getFreeTransactionLimit()) {
                                    // Aplicar comisión
                                    finalAmount = finalAmount.subtract(transactionConfig.getCommissionFee());
                                }

                                transaction.setAmount(finalAmount);
                                transaction.setTimestamp(LocalDateTime.now());
                                transaction.setProviderName(request.getProviderName());
                                transaction.setReferenceNumber(request.getReferenceNumber());
                                transaction.setDescription("Pago a " + request.getProviderName() + " - " +
                                        request.getCreditProductId());

                                return transactionRepository.save(transaction);
                            });
                });
    }

    // NUEVA FUNCIONALIDAD 2: Últimos 10 movimientos de tarjetas
    @Override
    public Flux<Transaction> getLastCardMovements(String accountId, String cardId, int limit) {
        int movementLimit = limit > 0 ? limit : 10; // Default to 10 if not specified

        return accountCustomer.accountExists(accountId)
                .flatMapMany(exists -> {
                    if (!exists) {
                        return Flux.error(new BusinessException("La cuenta no existe"));
                    }

                    // Utilizamos el método del repositorio con paginación
                    return transactionRepository.findByAccountIdAndRelatedEntityIdOrderByTimestampDesc(
                            accountId, cardId, PageRequest.of(0, movementLimit));
                });
    }
}

