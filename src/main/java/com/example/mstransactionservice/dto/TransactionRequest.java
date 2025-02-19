package com.example.mstransactionservice.dto;

import com.example.mstransactionservice.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private String accountId;
    private String relatedEntityId;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal commission; //comision
}