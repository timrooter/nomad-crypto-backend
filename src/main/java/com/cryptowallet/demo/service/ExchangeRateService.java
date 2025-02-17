package com.cryptowallet.demo.service;

import com.cryptowallet.demo.exception.ExchangeRateNotFoundException;
import com.cryptowallet.demo.exception.ResourceNotFoundException;
import com.cryptowallet.demo.model.ExchangeRate;
import com.cryptowallet.demo.model.User;
import com.cryptowallet.demo.model.Wallet;
import com.cryptowallet.demo.repository.ExchangeRateRepository;
import com.cryptowallet.demo.repository.UserRepository;
import com.cryptowallet.demo.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExchangeRateService {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Cacheable("exchangeRates")
    public Map<String, Double> getCurrentExchangeRates() {
        String url = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum,binancecoin,solana,ripple,tether&vs_currencies=usd";
        Map<String, Map<String, Object>> response = restTemplate.getForObject(url, HashMap.class);

        Map<String, Double> exchangeRatesToUsd = new HashMap<>();
        response.forEach((currency, rateMap) -> {
            Object rate = rateMap.get("usd");
            if (rate instanceof Integer) {
                exchangeRatesToUsd.put(currency, ((Integer) rate).doubleValue());
            } else if (rate instanceof Double) {
                exchangeRatesToUsd.put(currency, (Double) rate);
            }
        });

        double usdToKztRate = getUsdToKztRate();

        Map<String, Double> currentExchangeRates = new HashMap<>();
        exchangeRatesToUsd.forEach((currency, rateToUsd) -> {
            currentExchangeRates.put(currency + "_to_usd", rateToUsd);
            currentExchangeRates.put(currency + "_to_kzt", rateToUsd * usdToKztRate);
            currentExchangeRates.put("usd_to_" + currency, 1 / rateToUsd);
            currentExchangeRates.put("kzt_to_" + currency, 1 / rateToUsd * usdToKztRate);
        });

        // Adding direct USD to KZT rate and KZT to USD rate
        currentExchangeRates.put("usd_to_kzt", usdToKztRate);
        currentExchangeRates.put("kzt_to_usd", 1 / usdToKztRate);
        currentExchangeRates.put("usd_to_usd", 1.0);

        return currentExchangeRates;
    }

    @Cacheable(value = "exchangeRates", key = "'usdToKzt'")
    public double getUsdToKztRate() {
        String url = "https://api.exchangerate-api.com/v4/latest/USD";
        Map<String, Object> response = restTemplate.getForObject(url, HashMap.class);
        Map<String, Double> rates = (Map<String, Double>) response.get("rates");
        return rates.get("KZT");
    }

    public double exchangeCurrency(String fromCurrency, String toCurrency, Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "id", walletId));

        // Проверка владельца кошелька
        User currentUser = getCurrentUser();
        if (!wallet.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("You are not authorized to perform this transaction on this wallet");
        }

        Map<String, Double> currentRates = getCurrentExchangeRates();

        // Обработка обмена между фиатными валютами без изменений
        if ((fromCurrency.equalsIgnoreCase("usd") && toCurrency.equalsIgnoreCase("kzt")) ||
                (fromCurrency.equalsIgnoreCase("kzt") && toCurrency.equalsIgnoreCase("usd"))) {
            String rateKey = fromCurrency.toLowerCase() + "_to_" + toCurrency.toLowerCase();
            if (!currentRates.containsKey(rateKey)) {
                throw new ExchangeRateNotFoundException("Exchange rate not available for currency: " + rateKey);
            }
            return currentRates.get(rateKey);
        }

        // Обработка обмена криптовалюты на USD или KZT
        if (!fromCurrency.equalsIgnoreCase("usd") && !fromCurrency.equalsIgnoreCase("kzt") && !toCurrency.equalsIgnoreCase("usd") && !toCurrency.equalsIgnoreCase("kzt")) {
            throw new ExchangeRateNotFoundException("Direct exchange between cryptocurrencies is not allowed. Please exchange to USD or KZT first.");
        }

        double fromToUsdRate;
        double usdToToCurrencyRate;

        if (fromCurrency.equalsIgnoreCase("kzt")) {
            fromToUsdRate = 1 / currentRates.get("usd_to_kzt");
        } else {
            String fromKey = fromCurrency.toLowerCase() + "_to_usd";
            if (!currentRates.containsKey(fromKey)) {
                throw new ExchangeRateNotFoundException("Exchange rate not available for currency: " + fromCurrency);
            }
            fromToUsdRate = currentRates.get(fromKey);
        }

        if (toCurrency.equalsIgnoreCase("kzt")) {
            usdToToCurrencyRate = currentRates.get("usd_to_kzt");
        } else {
            String toKey = "usd_to_" + toCurrency.toLowerCase();
            if (!currentRates.containsKey(toKey)) {
                throw new ExchangeRateNotFoundException("Exchange rate not available for currency: " + toCurrency);
            }
            usdToToCurrencyRate = currentRates.get(toKey);
        }

        return fromToUsdRate * usdToToCurrencyRate;
    }


    @Scheduled(fixedRate = 60000) // обновление каждые 60 секунд
    @CacheEvict(value = "exchangeRates", allEntries = true)
    public void updateExchangeRates() {
        Map<String, Double> currentRates = getCurrentExchangeRates();
        currentRates.forEach((currency, rate) -> {
            String[] currencyPair = currency.split("_to_");
            String currencyCode = currencyPair[0];
            ExchangeRate exchangeRate = exchangeRateRepository.findByCurrency(currencyCode)
                    .orElse(new ExchangeRate());
            exchangeRate.setCurrency(currencyCode);
            exchangeRate.setRateToUsd(currentRates.get(currencyCode + "_to_usd"));
            exchangeRate.setRateToKzt(currentRates.get(currencyCode + "_to_kzt"));
            exchangeRate.setTimestamp(LocalDateTime.now());
            exchangeRateRepository.save(exchangeRate);
        });
    }


    // Additional methods for CRUD operations

    public List<ExchangeRate> getExchangeRates() {
        return exchangeRateRepository.findAll();
    }

    public ExchangeRate getExchangeRateById(Long id) {
        return exchangeRateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExchangeRate", "id", id));
    }

    public ExchangeRate createExchangeRate(ExchangeRate exchangeRate) {
        return exchangeRateRepository.save(exchangeRate);
    }

    public ExchangeRate updateExchangeRate(Long id, ExchangeRate exchangeRateDetails) {
        ExchangeRate exchangeRate = getExchangeRateById(id);
        exchangeRate.setCurrency(exchangeRateDetails.getCurrency());
        exchangeRate.setRateToUsd(exchangeRateDetails.getRateToUsd());
        exchangeRate.setRateToKzt(exchangeRateDetails.getRateToKzt());
        exchangeRate.setTimestamp(exchangeRateDetails.getTimestamp());
        return exchangeRateRepository.save(exchangeRate);
    }

    public void deleteExchangeRate(Long id) {
        ExchangeRate exchangeRate = getExchangeRateById(id);
        exchangeRateRepository.delete(exchangeRate);
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
