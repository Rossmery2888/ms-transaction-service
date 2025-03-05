package com.nttdata.bankapp.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO para solicitar pago a crédito de terceros.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThirdPartyPaymentRequest {
    @NotBlank(message = "Source account ID is required")
    private String sourceAccountId;

    @NotBlank(message = "Third party product ID is required")
    private String thirdPartyProductId;

    @NotBlank(message = "Third party product type is required")
    private String thirdPartyProductType; // CREDIT, CREDIT_CARD

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String description;

    @NotBlank(message = "Customer ID is required")
    private String customerId;
}