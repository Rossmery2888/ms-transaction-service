package com.nttdata.bankapp.transaction.model;


/**
 * Tipos de transacción actualizados.
 */
public enum TransactionType {
    DEPOSIT,       // Depósito en cuenta
    WITHDRAWAL,    // Retiro de cuenta
    TRANSFER,      // Transferencia entre cuentas
    PAYMENT,       // Pago de crédito o tarjeta
    CONSUMPTION,   // Consumo con tarjeta de crédito
    DEBIT_PAYMENT  // Pago con tarjeta de débito
}
