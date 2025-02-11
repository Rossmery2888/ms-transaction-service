package com.example.mstransactionservice.model;

import com.example.mstransactionservice.model.enums.TransactionType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String accountId;
    private String customerId;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String description;
    private BigDecimal balanceAfterTransaction;
}