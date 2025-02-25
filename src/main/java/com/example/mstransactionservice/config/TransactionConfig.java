package com.example.mstransactionservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Getter
@Configuration
public class TransactionConfig {
    @Value("${transaction.free.limit:20}")
    private Integer freeTransactionLimit;

    @Value("${transaction.commission.fee:2.5}")
    private BigDecimal commissionFee;

}
