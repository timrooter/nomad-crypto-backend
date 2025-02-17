package com.cryptowallet.demo.rest.controller;

import com.cryptowallet.demo.mapper.TransactionMapper;
import com.cryptowallet.demo.model.Transaction;
import com.cryptowallet.demo.rest.dto.CreateTransactionByWalletIdRequest;
import com.cryptowallet.demo.rest.dto.CreateTransactionRequest;
import com.cryptowallet.demo.rest.dto.TransactionDto;
import com.cryptowallet.demo.rest.dto.TransactionRequestByBankCardDto;
import com.cryptowallet.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public List<TransactionDto> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return transactions.stream()
                .map(TransactionMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TransactionDto getTransactionById(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return TransactionMapper.INSTANCE.toDto(transaction);
    }

    @PostMapping
    public TransactionDto createTransaction(@RequestBody CreateTransactionRequest createTransactionRequest) {
        Transaction transaction = transactionService.createTransaction(createTransactionRequest);
        return TransactionMapper.INSTANCE.toDto(transaction);
    }

    @PostMapping("/card")
    public TransactionDto createTransactionByBankCard(@RequestBody TransactionRequestByBankCardDto transactionRequestByBankCardDto) {
        Transaction transaction = transactionService.validateAndProcessTransaction(transactionRequestByBankCardDto);
        return TransactionMapper.INSTANCE.toDto(transaction);
    }




    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
    }
}
