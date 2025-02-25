package com.example.mstransactionservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThirdPartyPaymentRequest {
    private String accountId;
    private String creditProductId;
    private String providerName;
    private BigDecimal amount;
    private String referenceNumber;
}
