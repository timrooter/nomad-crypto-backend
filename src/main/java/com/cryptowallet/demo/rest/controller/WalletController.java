package com.cryptowallet.demo.rest.controller;

import com.cryptowallet.demo.mapper.WalletMapper;
import com.cryptowallet.demo.model.Wallet;
import com.cryptowallet.demo.rest.dto.TransferRequestDto;
import com.cryptowallet.demo.rest.dto.WalletDto;
import com.cryptowallet.demo.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping
    public WalletDto getWalletById() {
        Wallet wallet = walletService.getWallet();
        return WalletMapper.INSTANCE.toDto(wallet);
    }

    @PostMapping("/transfer")
    public WalletDto transferCurrency(@RequestBody TransferRequestDto transferRequest) {
        Wallet wallet = walletService.transferCurrency(transferRequest.getFromCurrency(), transferRequest.getToCurrency(), transferRequest.getAmount());
        return WalletMapper.INSTANCE.toDto(wallet);
    }
}
