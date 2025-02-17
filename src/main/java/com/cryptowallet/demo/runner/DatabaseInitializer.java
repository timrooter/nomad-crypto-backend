package com.cryptowallet.demo.runner;

import com.cryptowallet.demo.model.User;
import com.cryptowallet.demo.model.Wallet;
import com.cryptowallet.demo.security.WebSecurityConfig;
import com.cryptowallet.demo.service.UserService;
import com.cryptowallet.demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final UserService userService;
    private final WalletService walletService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userService.getAllUsers().isEmpty()) {
            return;
        }

        USERS.forEach(user -> {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.saveUser(user);
        });

        User user = userService.getUserByUsername("timrooter").orElseThrow(() -> new RuntimeException("User not found"));
        createWalletForUser(user);
        User user2 = userService.getUserByUsername("user").orElseThrow(() -> new RuntimeException("User not found"));
        createWalletForUser(user2);

        log.info("Database initialized");
    }

    private static final List<User> USERS = Arrays.asList(
            new User("admin", "admin", "Admin", "admin@mycompany.com", WebSecurityConfig.ADMIN),
            new User("user", "user", "User", "user@mycompany.com", WebSecurityConfig.USER),
            new User("timrooter", "tim", "Timur Inamkhojayev", "tim@gmail.com", WebSecurityConfig.USER)

    );

    private void createWalletForUser(User user) {
        Wallet wallet = user.getWallet();
        wallet.setUser(user);
        wallet.setUsd(100000.0);
        wallet.setBitcoin(0.01);
        wallet.setEthereum(0.5);
        wallet.setBinancecoin(10.0);
        wallet.setSolana(20.0);
        wallet.setRipple(3000.0);
        wallet.setTether(500.0);

        walletService.createWallet(wallet);
    }
}
