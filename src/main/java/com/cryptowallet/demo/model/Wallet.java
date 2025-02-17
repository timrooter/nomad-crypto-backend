package com.cryptowallet.demo.model;

import com.cryptowallet.demo.service.CardUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "wallets")
@AllArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @OneToOne(mappedBy = "wallet", cascade = CascadeType.ALL)
    private BankCard bankCard;

    private double ethereum;
    private double ripple;
    private double tether;
    private double binancecoin;
    private double solana;
    private double bitcoin;
    private double usd;
    private double kzt;

    @PrePersist
    public void prePersist() {
        BankCard newBankCard = new BankCard();
        newBankCard.setWallet(this);
        newBankCard.setCardNumber(CardUtils.generateUniqueCardNumber());
        newBankCard.setCardHolderName(this.user.getName());
        newBankCard.setExpiryDate(CardUtils.generateExpiryDate());
        newBankCard.setCvv(CardUtils.generateCvv());
        this.bankCard = newBankCard;
    }

    public void addBalance(String currency, double amount) {
        switch (currency) {
            case "ethereum":
                ethereum += amount;
                break;
            case "ripple":
                ripple += amount;
                break;
            case "tether":
                tether += amount;
                break;
            case "binancecoin":
                binancecoin += amount;
                break;
            case "solana":
                solana += amount;
                break;
            case "bitcoin":
                bitcoin += amount;
                break;
            case "usd":
                usd += amount;
                break;
            case "kzt":
                kzt += amount;
                break;
            default:
                throw new IllegalArgumentException("Unknown currency: " + currency);
        }
    }

    public boolean subtractBalance(String currency, double amount) {
        switch (currency) {
            case "ethereum":
                if (ethereum < amount) return false;
                ethereum -= amount;
                break;
            case "ripple":
                if (ripple < amount) return false;
                ripple -= amount;
                break;
            case "tether":
                if (tether < amount) return false;
                tether -= amount;
                break;
            case "binancecoin":
                if (binancecoin < amount) return false;
                binancecoin -= amount;
                break;
            case "solana":
                if (solana < amount) return false;
                solana -= amount;
                break;
            case "bitcoin":
                if (bitcoin < amount) return false;
                bitcoin -= amount;
                break;
            case "usd":
                if (usd < amount) return false;
                usd -= amount;
                break;
            case "kzt":
                if (kzt < amount) return false;
                kzt -= amount;
                break;
            default:
                throw new IllegalArgumentException("Unknown currency: " + currency);
        }
        return true;
    }

    public double getBalance(String currency) {
        switch (currency) {
            case "ethereum":
                return ethereum;
            case "ripple":
                return ripple;
            case "tether":
                return tether;
            case "binancecoin":
                return binancecoin;
            case "solana":
                return solana;
            case "bitcoin":
                return bitcoin;
            case "usd":
                return usd;
            case "kzt":
                return kzt;
            default:
                throw new IllegalArgumentException("Unknown currency: " + currency);
        }
    }
}
