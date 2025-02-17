package com.cryptowallet.demo.repository;

import com.cryptowallet.demo.model.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {
    Optional<BankCard> findByWalletId(Long walletId);
    Optional<BankCard> findByCardNumber(String cardNumber);
}
