package com.example.mstransactionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal amount;
    private String description;
}