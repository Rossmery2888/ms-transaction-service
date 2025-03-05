package com.nttdata.bankapp.transaction.dto;
import com.nttdata.bankapp.transaction.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para la transferencia de datos de transacciones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private String id;
    private String accountId;
    private String creditId;
    private String creditCardId;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String description;
    private LocalDateTime transactionDate;
    private String customerId;
    private String referenceNumber;
}