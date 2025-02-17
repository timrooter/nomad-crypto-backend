package com.cryptowallet.demo.mapper;

import com.cryptowallet.demo.model.ExchangeRate;
import com.cryptowallet.demo.rest.dto.ExchangeRateDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExchangeRateMapper {
    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    ExchangeRate toEntity(ExchangeRateDto dto);

    ExchangeRateDto toDto(ExchangeRate entity);
}
