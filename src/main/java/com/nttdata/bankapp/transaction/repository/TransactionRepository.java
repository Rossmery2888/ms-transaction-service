package com.nttdata.bankapp.transaction.repository;

import com.nttdata.bankapp.transaction.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

/**
 * Repositorio para operaciones CRUD en la colección de transacciones.
 */
@Repository
public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
    Flux<Transaction> findByAccountId(String accountId);
    Flux<Transaction> findByCreditId(String creditId);
    Flux<Transaction> findByCreditCardId(String creditCardId);
    Flux<Transaction> findByCustomerId(String customerId);
    Flux<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    Flux<Transaction> findByAccountIdAndTransactionDateBetween(String accountId, LocalDateTime startDate, LocalDateTime endDate);
}
