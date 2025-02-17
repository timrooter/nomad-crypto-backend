package com.cryptowallet.demo.service;

import com.cryptowallet.demo.exception.InsufficientBalanceException;
import com.cryptowallet.demo.exception.ResourceNotFoundException;
import com.cryptowallet.demo.model.User;
import com.cryptowallet.demo.model.Wallet;
import com.cryptowallet.demo.repository.UserRepository;
import com.cryptowallet.demo.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @PreAuthorize("@walletService.isWalletOwner(#id)")
    public Wallet getWalletById(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "id", id));
    }

    public Wallet getWallet() {
        User currentUser = getCurrentUser();
        return walletRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "user_id", currentUser.getId()));
    }

    public Wallet createWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    @PreAuthorize("@walletService.isWalletOwner(#id)")
    public Wallet updateWallet(Long id, Wallet walletDetails) {
        Wallet wallet = getWalletById(id);
        wallet.setEthereum(walletDetails.getEthereum());
        wallet.setRipple(walletDetails.getRipple());
        wallet.setTether(walletDetails.getTether());
        wallet.setBinancecoin(walletDetails.getBinancecoin());
        wallet.setSolana(walletDetails.getSolana());
        wallet.setBitcoin(walletDetails.getBitcoin());
        wallet.setUsd(walletDetails.getUsd());
        wallet.setKzt(walletDetails.getKzt());
        return walletRepository.save(wallet);
    }

    @PreAuthorize("@walletService.isWalletOwner(#id)")
    public void deleteWallet(Long id) {
        Wallet wallet = getWalletById(id);
        walletRepository.delete(wallet);
    }

    @Transactional
    public Wallet transferCurrency(String fromCurrency, String toCurrency, double amount) {
        User currentUser = getCurrentUser();
        Wallet wallet = walletRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "user_id", currentUser.getId()));

        double fromBalance = getBalance(wallet, fromCurrency);
        if (fromBalance < amount) {
            throw new InsufficientBalanceException("Insufficient balance in " + fromCurrency);
        }

        double exchangeRate = exchangeRateService.exchangeCurrency(fromCurrency, toCurrency, wallet.getId());

        double convertedAmount = amount * exchangeRate;

        updateBalance(wallet, fromCurrency, fromBalance - amount);
        updateBalance(wallet, toCurrency, getBalance(wallet, toCurrency) + convertedAmount);

        return walletRepository.save(wallet);
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
            default: throw new IllegalArgumentException("Unknown currency: " + currency);
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
            default: throw new IllegalArgumentException("Unknown currency: " + currency);
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));
    }

    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "user_id", userId));
    }

}
