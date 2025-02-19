package com.example.mstransactionservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Data
@Configuration
@ConfigurationProperties(prefix = "transaction")
public class TransactionProperties {
    private int freeTransactionsLimit = 20;
    private BigDecimal commissionRate = new BigDecimal("1.00"); // $1.00 per transaction after limit
}
