package com.example.mstransactionservice.dto.request;

import com.example.mstransactionservice.model.enums.TransactionType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequestDTO {
    private String accountId;
    private String customerId;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
}