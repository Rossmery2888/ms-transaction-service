package com.example.mstransactionservice.repository;

import com.example.mstransactionservice.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, String> {
    Flux<Transaction> findByAccountId(String accountId);
    Flux<Transaction> findByRelatedEntityId(String relatedEntityId);
    Flux<Transaction> findByAccountIdAndRelatedEntityIdOrderByTimestampDesc(
            String accountId, String relatedEntityId, Pageable pageable);
}
