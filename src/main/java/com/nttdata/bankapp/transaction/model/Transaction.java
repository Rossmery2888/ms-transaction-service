package com.nttdata.bankapp.transaction.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo actualizado de transacción, incluyendo operaciones con tarjeta de débito.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String accountId; // ID de la cuenta asociada (origen para transferencias)
    private String destinationAccountId; // ID de la cuenta destino (solo para transferencias)
    private String creditId; // ID del crédito asociado (opcional)
    private String creditCardId; // ID de la tarjeta de crédito asociada (opcional)
    private String debitCardId; // ID de la tarjeta de débito asociada (opcional)
    private String thirdPartyProductId; // ID del producto de un tercero (para pagos a terceros)
    private TransactionType type; // DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT, CONSUMPTION, DEBIT_PAYMENT
    private BigDecimal amount;
    private BigDecimal fee; // Comisión cobrada (si aplica)
    private String description;
    private LocalDateTime transactionDate;
    private String customerId; // ID del cliente que realizó la transacción
    private String referenceNumber; // Número de referencia único para la transacción
}
