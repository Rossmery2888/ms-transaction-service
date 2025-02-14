package com.example.mstransactionservice.service.Impl;


import com.example.mstransactionservice.model.Transaction;
import com.example.mstransactionservice.dto.TransactionRequest;
import com.example.mstransactionservice.repository.TransactionRepository;
import com.example.mstransactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    @Override
    public Mono<Transaction> registerTransaction(TransactionRequest request) {
        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),
                request.getAccountId(),
                request.getRelatedEntityId(),
                request.getType(),
                request.getAmount(),
                LocalDateTime.now()
        );
        return transactionRepository.save(transaction);
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
