package com.example.mstransactionservice.service;


import com.example.mstransactionservice.dto.ThirdPartyPaymentRequest;
import com.example.mstransactionservice.dto.TransferRequest;
import com.example.mstransactionservice.model.Transaction;
import com.example.mstransactionservice.dto.TransactionRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.time.YearMonth;

public interface TransactionService {
    Mono<Transaction> registerTransaction(TransactionRequest request);
    Flux<Transaction> getTransactionsByAccountId(String accountId);
    Flux<Transaction> getTransactionsByRelatedEntityId(String relatedEntityId);
    Mono<Integer> getTransactionCountForMonth(String accountId, YearMonth yearMonth);
    Mono<Transaction> transferInternal(TransferRequest request);
    Mono<Transaction> transferExternal(TransferRequest request);

    Mono<Transaction> registerThirdPartyPayment(ThirdPartyPaymentRequest request);
    Flux<Transaction> getLastCardMovements(String accountId, String cardId, int limit);
}