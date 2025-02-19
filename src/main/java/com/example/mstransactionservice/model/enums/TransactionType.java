package com.example.mstransactionservice.model.enums;

public enum TransactionType {
    DEPOSIT, WITHDRAWAL, PAYMENT, CONSUMPTION,
    INTERNAL_TRANSFER, // Transfer between same client accounts
    THIRD_PARTY_TRANSFER // Transfer to other clients
}