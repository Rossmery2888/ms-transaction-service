package com.example.mstransactionservice.model.enums;

public enum TransactionType {
    DEPOSIT, WITHDRAWAL, PAYMENT, CONSUMPTION,
    TRANSFER_INTERNAL, // Transfer between same client accounts
    TRANSFER_EXTERNAL, // Transfer to other clients
    THIRD_PARTY_PAYMENT //
}