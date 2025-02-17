package com.cryptowallet.demo.service;

import com.cryptowallet.demo.model.BankCard;
import com.cryptowallet.demo.model.User;
import com.cryptowallet.demo.repository.BankCardRepository;
import com.cryptowallet.demo.repository.UserRepository;
import com.cryptowallet.demo.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class BankCardService {

    @Autowired
    private BankCardRepository bankCardRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    public BankCard getBankCardForCurrentUser() {
        User currentUser = getCurrentUser();
        return bankCardRepository.findByWalletId(currentUser.getWallet().getId())
                .orElseThrow(() -> new RuntimeException("Bank card not found"));
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
