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
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    private String id;
    private String accountId;
    private String relatedEntityId;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal commission;
    private LocalDateTime timestamp;
    private String description;
}
