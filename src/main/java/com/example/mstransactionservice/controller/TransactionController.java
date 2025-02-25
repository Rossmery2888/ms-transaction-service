package com.example.mstransactionservice.controller;


import com.example.mstransactionservice.dto.ThirdPartyPaymentRequest;
import com.example.mstransactionservice.dto.TransactionRequest;
import com.example.mstransactionservice.dto.TransferRequest;
import com.example.mstransactionservice.model.Transaction;
import com.example.mstransactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public Mono<Transaction> registerTransaction(@RequestBody TransactionRequest request) {
        return transactionService.registerTransaction(request);
    }

    @GetMapping("/account/{accountId}")
    public Flux<Transaction> getTransactionsByAccountId(@PathVariable String accountId) {
        return transactionService.getTransactionsByAccountId(accountId);
    }

    @GetMapping("/entity/{relatedEntityId}")
    public Flux<Transaction> getTransactionsByRelatedEntityId(@PathVariable String relatedEntityId) {
        return transactionService.getTransactionsByRelatedEntityId(relatedEntityId);
    }

    @GetMapping("/account/{accountId}/count")
    public Mono<Integer> getTransactionCount(@PathVariable String accountId, @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month) {
        YearMonth yearMonth;
        if (year != null && month != null) {
            yearMonth = YearMonth.of(year, month);
        } else {
            yearMonth = YearMonth.now();
        }
        return transactionService.getTransactionCountForMonth(accountId, yearMonth);
    }

    @PostMapping("/transfer/internal")
    public Mono<Transaction> transferInternal(@RequestBody TransferRequest request) {
        return transactionService.transferInternal(request);
    }

    @PostMapping("/transfer/external")
    public Mono<Transaction> transferExternal(@RequestBody TransferRequest request) {
        return transactionService.transferExternal(request);
    }

   // Registrar pagos de terceros
    @PostMapping("/payment/third-party")
    public Mono<Transaction> registerThirdPartyPayment(@RequestBody ThirdPartyPaymentRequest request) {
        return transactionService.registerThirdPartyPayment(request);
    }

    // Últimos movimientos de tarjeta
    @GetMapping("/card-movements/{accountId}/{cardId}")
    public Flux<Transaction> getLastCardMovements(
            @PathVariable String accountId,
            @PathVariable String cardId,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        return transactionService.getLastCardMovements(accountId, cardId, limit);
    }
}