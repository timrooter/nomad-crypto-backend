package com.cryptowallet.demo.repository;

import com.cryptowallet.demo.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
//    List<Wallet> findByUserId(Long userId);
Optional<Wallet> findByUserId(Long userId);

}
