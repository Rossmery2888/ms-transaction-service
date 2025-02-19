package com.example.mstransactionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private String id;
    private String clientId;
    private String accountNumber;
    private String accountType;
    private String status;
}