package com.cryptowallet.demo.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ExchangeRateDto {
    private Long id;
    private String currency;
    private double rateToUsd;
    private double rateToKzt;
    private LocalDateTime timestamp;
}
