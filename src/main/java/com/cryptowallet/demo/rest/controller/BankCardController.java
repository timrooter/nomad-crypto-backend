package com.cryptowallet.demo.rest.controller;

import com.cryptowallet.demo.model.BankCard;
import com.cryptowallet.demo.service.BankCardService;
import com.cryptowallet.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/card")
public class BankCardController {

    @Autowired
    private BankCardService bankCardService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public BankCard getBankCardForCurrentUser() {
        return bankCardService.getBankCardForCurrentUser();
    }

}
