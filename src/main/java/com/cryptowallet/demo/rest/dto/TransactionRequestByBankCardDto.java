package com.cryptowallet.demo.rest.dto;

import com.cryptowallet.demo.model.TransactionType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionRequestByBankCardDto {
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;
    private double amount;
    private String currency;
    private Long toUserId;
    private TransactionType transactionType;
}
