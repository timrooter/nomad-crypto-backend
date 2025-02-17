package com.cryptowallet.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    private double amount;
    private String currency;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private LocalDateTime timestamp = LocalDateTime.now();

    public Transaction(User fromUser, User toUser, double amount, String currency, TransactionType transactionType) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.timestamp = LocalDateTime.now();
    }
}
