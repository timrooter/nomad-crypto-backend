package com.cryptowallet.demo.rest.controller;

import com.cryptowallet.demo.mapper.ExchangeRateMapper;
import com.cryptowallet.demo.model.ExchangeRate;
import com.cryptowallet.demo.rest.dto.ExchangeRateDto;
import com.cryptowallet.demo.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exchange-rates")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping
    public List<ExchangeRateDto> getAllExchangeRates() {
        List<ExchangeRate> exchangeRates = exchangeRateService.getExchangeRates();
        return exchangeRates.stream()
                .map(ExchangeRateMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ExchangeRateDto getExchangeRateById(@PathVariable Long id) {
        ExchangeRate exchangeRate = exchangeRateService.getExchangeRateById(id);
        return ExchangeRateMapper.INSTANCE.toDto(exchangeRate);
    }

//    @PostMapping
//    public ExchangeRateDto createExchangeRate(@RequestBody ExchangeRateDto exchangeRateDto) {
//        ExchangeRate exchangeRate = ExchangeRateMapper.INSTANCE.toEntity(exchangeRateDto);
//        exchangeRate = exchangeRateService.createExchangeRate(exchangeRate);
//        return ExchangeRateMapper.INSTANCE.toDto(exchangeRate);
//    }
//
//    @PutMapping("/{id}")
//    public ExchangeRateDto updateExchangeRate(@PathVariable Long id, @RequestBody ExchangeRateDto exchangeRateDto) {
//        ExchangeRate exchangeRate = ExchangeRateMapper.INSTANCE.toEntity(exchangeRateDto);
//        exchangeRate = exchangeRateService.updateExchangeRate(id, exchangeRate);
//        return ExchangeRateMapper.INSTANCE.toDto(exchangeRate);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteExchangeRate(@PathVariable Long id) {
//        exchangeRateService.deleteExchangeRate(id);
//    }

    @GetMapping("/current")
    public Map<String, Double> getCurrentExchangeRates() {
        return exchangeRateService.getCurrentExchangeRates();
    }
}
