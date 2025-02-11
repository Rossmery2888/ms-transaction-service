package com.example.mstransactionservice.dto.response;

import com.example.mstransactionservice.model.enums.TransactionType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponseDTO {
    private String id;
    private String accountId;
    private String customerId;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String description;
    private BigDecimal balanceAfterTransaction;
}