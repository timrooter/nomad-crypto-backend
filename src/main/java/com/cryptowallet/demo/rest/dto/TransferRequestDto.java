package com.cryptowallet.demo.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransferRequestDto {
    private String fromCurrency;
    private String toCurrency;
    private double amount;
}

