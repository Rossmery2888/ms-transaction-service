package com.example.mstransactionservice.repository;

import com.example.mstransactionservice.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
    Flux<Transaction> findByAccountId(String accountId);
    Flux<Transaction> findByCustomerId(String customerId);
}