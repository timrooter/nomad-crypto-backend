package com.cryptowallet.demo.rest.dto;

import com.cryptowallet.demo.model.TransactionType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateTransactionRequest {
    private String toUsername;
    private double amount;
    private String currency;
    private TransactionType transactionType;
}
