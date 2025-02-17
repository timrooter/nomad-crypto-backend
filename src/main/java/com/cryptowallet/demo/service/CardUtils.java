package com.cryptowallet.demo.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CardUtils {

    public static String generateUniqueCardNumber() {
        Random rand = new Random();
        StringBuilder cardNumber = new StringBuilder("4400");
        for (int i = 0; i < 12; i++) {
            cardNumber.append(rand.nextInt(10));
        }
        return cardNumber.toString();
    }

    public static String generateExpiryDate() {
        LocalDate expiryDate = LocalDate.now().plusYears(2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return expiryDate.format(formatter);
    }

    public static String generateCvv() {
        Random rand = new Random();
        int cvv = rand.nextInt(900) + 100; // Генерирует случайное число от 100 до 999
        return String.valueOf(cvv);
    }
}
