package com.example.mstransactionservice.service.Impl;


import com.example.mstransactionservice.Exception.BusinessException;
import com.example.mstransactionservice.config.TransactionProperties;
import com.example.mstransactionservice.dto.AccountDTO;
import com.example.mstransactionservice.dto.TransferRequest;
import com.example.mstransactionservice.model.Transaction;
import com.example.mstransactionservice.dto.TransactionRequest;
import com.example.mstransactionservice.model.enums.TransactionType;
import com.example.mstransactionservice.repository.TransactionRepository;
import com.example.mstransactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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
    private final TransactionProperties transactionProperties;
    private final WebClient accountServiceClient;

    @Override
    public Mono<Transaction> registerTransaction(TransactionRequest request) {
        return calculateCommission(request.getAccountId())
                .flatMap(commission -> {
                    Transaction transaction = new Transaction(
                            UUID.randomUUID().toString(),
                            request.getAccountId(),
                            request.getRelatedEntityId(),
                            request.getType(),
                            request.getAmount(),
                            commission,
                            LocalDateTime.now(),
                            null
                    );
                    return transactionRepository.save(transaction);
                });
    }

    @Override
    public Mono<Transaction> processTransfer(TransferRequest request) {
        return validateAccounts(request.getSourceAccountId(), request.getDestinationAccountId())
                .flatMap(isInternalTransfer -> {
                    TransactionType transferType = isInternalTransfer ?
                            TransactionType.INTERNAL_TRANSFER :
                            TransactionType.THIRD_PARTY_TRANSFER;

                    return calculateCommission(request.getSourceAccountId())
                            .flatMap(commission -> {
                                // Create debit transaction
                                Transaction debitTx = new Transaction(
                                        UUID.randomUUID().toString(),
                                        request.getSourceAccountId(),
                                        request.getDestinationAccountId(),
                                        transferType,
                                        request.getAmount().negate(),
                                        commission,
                                        LocalDateTime.now(),
                                        request.getDescription()
                                );

                                // Create credit transaction
                                Transaction creditTx = new Transaction(
                                        UUID.randomUUID().toString(),
                                        request.getDestinationAccountId(),
                                        request.getSourceAccountId(),
                                        transferType,
                                        request.getAmount(),
                                        BigDecimal.ZERO,
                                        LocalDateTime.now(),
                                        request.getDescription()
                                );

                                return transactionRepository.save(debitTx)
                                        .then(transactionRepository.save(creditTx))
                                        .thenReturn(debitTx);
                            });
                });
    }

    private Mono<Boolean> validateAccounts(String sourceAccountId, String destinationAccountId) {
        return accountServiceClient.get()
                .uri("/api/accounts/{accountId}", sourceAccountId)
                .retrieve()
                .bodyToMono(AccountDTO.class)
                .flatMap(sourceAccount ->
                        accountServiceClient.get()
                                .uri("/api/accounts/{accountId}", destinationAccountId)
                                .retrieve()
                                .bodyToMono(AccountDTO.class)
                                .map(destAccount -> sourceAccount.getClientId().equals(destAccount.getClientId()))
                )
                .onErrorResume(e -> Mono.error(new BusinessException("Invalid account information")));
    }

    private Mono<BigDecimal> calculateCommission(String accountId) {
        LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
        return transactionRepository.findByAccountId(accountId)
                .filter(tx -> tx.getTimestamp().isAfter(startOfMonth))
                .count()
                .map(count -> {
                    if (count >= transactionProperties.getFreeTransactionsLimit()) {
                        return transactionProperties.getCommissionRate();
                    }
                    return BigDecimal.ZERO;
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
}