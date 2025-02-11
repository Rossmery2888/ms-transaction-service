package com.example.mstransactionservice.service.Impl;

import com.example.mstransactionservice.dto.request.TransactionRequestDTO;
import com.example.mstransactionservice.dto.response.TransactionResponseDTO;
import com.example.mstransactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    @Override
    public Mono<TransactionResponseDTO> createCredit(TransactionRequestDTO request) {
        return null;
    }

    @Override
    public Mono<TransactionResponseDTO> getCreditById(String id) {
        return null;
    }

    @Override
    public Flux<TransactionResponseDTO> getCreditsByCustomerId(String customerId) {
        return null;
    }

    @Override
    public Mono<TransactionResponseDTO> makePayment(String creditId, BigDecimal amount) {
        return null;
    }
}
