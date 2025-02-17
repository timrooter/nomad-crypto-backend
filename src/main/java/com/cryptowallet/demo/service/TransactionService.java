package com.cryptowallet.demo.service;

import com.cryptowallet.demo.exception.InsufficientBalanceException;
import com.cryptowallet.demo.exception.InvalidCurrencyException;
import com.cryptowallet.demo.exception.InvalidTransactionException;
import com.cryptowallet.demo.exception.ResourceNotFoundException;
import com.cryptowallet.demo.model.*;
import com.cryptowallet.demo.repository.BankCardRepository;
import com.cryptowallet.demo.repository.TransactionRepository;
import com.cryptowallet.demo.repository.UserRepository;
import com.cryptowallet.demo.repository.WalletRepository;
import com.cryptowallet.demo.rest.dto.CreateTransactionByWalletIdRequest;
import com.cryptowallet.demo.rest.dto.CreateTransactionRequest;
import com.cryptowallet.demo.rest.dto.TransactionDto;
import com.cryptowallet.demo.rest.dto.TransactionRequestByBankCardDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private BankCardRepository bankCardRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;
    public List<Transaction> getAllTransactions() {
        User currentUser = getCurrentUser();
        return transactionRepository.findByFromUserIdOrToUserId(currentUser.getId(), currentUser.getId());
    }

    public Transaction getTransactionById(Long id) {
        User currentUser = getCurrentUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));

        if (!transaction.getFromUser().getId().equals(currentUser.getId()) &&
                !transaction.getToUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Transaction", "id", id);
        }

        return transaction;
    }

    public Transaction validateAndProcessTransaction(TransactionRequestByBankCardDto transactionRequestByBankCardDto) {
        BankCard fromBankCard = bankCardRepository.findByCardNumber(transactionRequestByBankCardDto.getCardNumber())
                .orElseThrow(() -> new InvalidTransactionException("Invalid card number"));

        if (!fromBankCard.getCvv().equals(transactionRequestByBankCardDto.getCvv())) {
            throw new InvalidTransactionException("Invalid CVV");
        }

        Wallet fromWallet = fromBankCard.getWallet();
        User toUser = userRepository.findById(transactionRequestByBankCardDto.getToUserId())
                .orElseThrow(() -> new InvalidTransactionException("Invalid recipient user ID"));
        Wallet toWallet = walletRepository.findByUserId(toUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "user_id", toUser.getId()));

        double fromBalance = getBalance(fromWallet, transactionRequestByBankCardDto.getCurrency());
        if (fromBalance < transactionRequestByBankCardDto.getAmount()) {
            throw new InsufficientBalanceException("Insufficient funds in " + transactionRequestByBankCardDto.getCurrency());
        }

        updateBalance(fromWallet, transactionRequestByBankCardDto.getCurrency(), fromBalance - transactionRequestByBankCardDto.getAmount());
        updateBalance(toWallet, transactionRequestByBankCardDto.getCurrency(), getBalance(toWallet, transactionRequestByBankCardDto.getCurrency()) + transactionRequestByBankCardDto.getAmount());

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        Transaction transaction = new Transaction();
        transaction.setFromUser(fromWallet.getUser());
        transaction.setToUser(toUser);
        transaction.setAmount(transactionRequestByBankCardDto.getAmount());
        transaction.setCurrency(transactionRequestByBankCardDto.getCurrency());
        transaction.setTransactionType(transactionRequestByBankCardDto.getTransactionType());
        transaction.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }


//    public Transaction createTransaction(CreateTransactionByWalletIdRequest createTransactionByWalletIdRequest) {
//        User currentUser = getCurrentUser();
//        Wallet fromWallet = walletRepository.findByUserId(currentUser.getId())
//                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "user_id", currentUser.getId()));
//
//        Wallet toWallet = walletRepository.findById(createTransactionByWalletIdRequest.getToWalletId())
//                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "id", createTransactionByWalletIdRequest.getToWalletId()));
//
//        double fromBalance = getBalance(fromWallet, createTransactionByWalletIdRequest.getCurrency());
//        if (fromBalance < createTransactionByWalletIdRequest.getAmount()) {
//            throw new InsufficientBalanceException("Insufficient funds in " + createTransactionByWalletIdRequest.getCurrency());
//        }
//
//        updateBalance(fromWallet, createTransactionByWalletIdRequest.getCurrency(), fromBalance - createTransactionByWalletIdRequest.getAmount());
//        updateBalance(toWallet, createTransactionByWalletIdRequest.getCurrency(), getBalance(toWallet, createTransactionByWalletIdRequest.getCurrency()) + createTransactionByWalletIdRequest.getAmount());
//
//        walletRepository.save(fromWallet);
//        walletRepository.save(toWallet);
//
//        Transaction transaction = new Transaction(currentUser, toWallet.getUser(), createTransactionByWalletIdRequest.getAmount(), createTransactionByWalletIdRequest.getCurrency(), createTransactionByWalletIdRequest.getTransactionType());
//        transaction.setTimestamp(LocalDateTime.now());
//        return transactionRepository.save(transaction);
//    }

    public Transaction createTransaction(CreateTransactionRequest createTransactionRequest) {
        User currentUser = getCurrentUser();
        Wallet fromWallet = walletRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "user_id", currentUser.getId()));

        User toUser = userService.validateAndGetUserByUsername(createTransactionRequest.getToUsername());
        Wallet toWallet = walletRepository.findByUserId(toUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "user_id", toUser.getId()));

        double fromBalance = getBalance(fromWallet, createTransactionRequest.getCurrency());
        if (fromBalance < createTransactionRequest.getAmount()) {
            throw new InsufficientBalanceException("Insufficient funds in " + createTransactionRequest.getCurrency());
        }

        updateBalance(fromWallet, createTransactionRequest.getCurrency(), fromBalance - createTransactionRequest.getAmount());
        updateBalance(toWallet, createTransactionRequest.getCurrency(), getBalance(toWallet, createTransactionRequest.getCurrency()) + createTransactionRequest.getAmount());

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        Transaction transaction = new Transaction(currentUser, toUser, createTransactionRequest.getAmount(), createTransactionRequest.getCurrency(), createTransactionRequest.getTransactionType());
        transaction.setTimestamp(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }



    public Transaction updateTransaction(Long id, TransactionDto transactionDto) {
        Transaction transaction = getTransactionById(id);
        transaction.setAmount(transactionDto.getAmount());
        transaction.setCurrency(transactionDto.getCurrency());
        transaction.setTransactionType(transactionDto.getTransactionType());
        transaction.setTimestamp(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        Transaction transaction = getTransactionById(id);
        transactionRepository.delete(transaction);
    }

    private double getBalance(Wallet wallet, String currency) {
        switch (currency.toLowerCase()) {
            case "usd": return wallet.getUsd();
            case "bitcoin": return wallet.getBitcoin();
            case "ethereum": return wallet.getEthereum();
            case "binancecoin": return wallet.getBinancecoin();
            case "solana": return wallet.getSolana();
            case "ripple": return wallet.getRipple();
            case "tether": return wallet.getTether();
            case "kzt": return wallet.getKzt();
            default: throw new InvalidCurrencyException("Unknown currency: " + currency);
        }
    }

    private void updateBalance(Wallet wallet, String currency, double amount) {
        switch (currency.toLowerCase()) {
            case "usd": wallet.setUsd(amount); break;
            case "bitcoin": wallet.setBitcoin(amount); break;
            case "ethereum": wallet.setEthereum(amount); break;
            case "binancecoin": wallet.setBinancecoin(amount); break;
            case "solana": wallet.setSolana(amount); break;
            case "ripple": wallet.setRipple(amount); break;
            case "tether": wallet.setTether(amount); break;
            case "kzt": wallet.setKzt(amount); break;
            default: throw new InvalidCurrencyException("Unknown currency: " + currency);
        }
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
