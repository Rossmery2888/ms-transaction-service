package com.example.mstransactionservice.model;


import com.example.mstransactionservice.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String accountId;
    private String relatedEntityId; // Credit ID, Card ID, or Account ID
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime timestamp;

    // New fields
    private String providerName;
    private String referenceNumber;
    private String cardNumber;
    private String description;
}

