package com.example.mstransactionservice.service;

import com.example.mstransactionservice.dto.request.TransactionRequestDTO;
import com.example.mstransactionservice.dto.response.TransactionResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface TransactionService {
    Mono<TransactionResponseDTO> createCredit(TransactionRequestDTO request);
    Mono<TransactionResponseDTO> getCreditById(String id);
    Flux<TransactionResponseDTO> getCreditsByCustomerId(String customerId);
    Mono<TransactionResponseDTO> makePayment(String creditId, BigDecimal amount);
}