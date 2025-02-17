package com.cryptowallet.demo.repository;

import com.cryptowallet.demo.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromUserIdOrToUserId(Long fromUserId, Long toUserId);
}
