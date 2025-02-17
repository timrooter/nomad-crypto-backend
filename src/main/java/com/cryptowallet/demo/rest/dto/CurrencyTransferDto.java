package com.cryptowallet.demo.rest.dto;

import lombok.Data;

@Data
public class CurrencyTransferDto {
    private Long walletId;
    private String fromCurrency;
    private String toCurrency;
    private double amount;
}
