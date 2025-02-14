package com.example.mstransactionservice.service;


import com.example.mstransactionservice.model.Transaction;
import com.example.mstransactionservice.dto.TransactionRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface TransactionService {
        Mono<Transaction> registerTransaction(TransactionRequest request);
        Flux<Transaction> getTransactionsByAccountId(String accountId);
        Flux<Transaction> getTransactionsByRelatedEntityId(String relatedEntityId);
    }
