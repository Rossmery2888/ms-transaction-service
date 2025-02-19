package com.example.mstransactionservice.controller;


import com.example.mstransactionservice.dto.TransactionRequest;
import com.example.mstransactionservice.dto.TransferRequest;
import com.example.mstransactionservice.model.Transaction;
import com.example.mstransactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public Mono<Transaction> registerTransaction(@RequestBody TransactionRequest request) {
        return transactionService.registerTransaction(request);
    }

    @PostMapping("/transfer")
    public Mono<Transaction> processTransfer(@RequestBody TransferRequest request) {
        return transactionService.processTransfer(request);
    }

    @GetMapping("/account/{accountId}")
    public Flux<Transaction> getTransactionsByAccountId(@PathVariable String accountId) {
        return transactionService.getTransactionsByAccountId(accountId);
    }

    @GetMapping("/entity/{relatedEntityId}")
    public Flux<Transaction> getTransactionsByRelatedEntityId(@PathVariable String relatedEntityId) {
        return transactionService.getTransactionsByRelatedEntityId(relatedEntityId);
    }
}