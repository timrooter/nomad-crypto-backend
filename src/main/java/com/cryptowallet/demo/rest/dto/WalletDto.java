package com.cryptowallet.demo.rest.dto;

import lombok.Data;

@Data
public class WalletDto {
    private Long id;
    private Long userId;
    private double ethereum;
    private double ripple;
    private double tether;
    private double binancecoin;
    private double solana;
    private double bitcoin;
    private double usd;
    private double kzt;
}
