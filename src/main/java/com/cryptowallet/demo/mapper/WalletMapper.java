package com.cryptowallet.demo.mapper;

import com.cryptowallet.demo.model.Wallet;
import com.cryptowallet.demo.rest.dto.WalletDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WalletMapper {
    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);

    @Mapping(source = "user.id", target = "userId")
    WalletDto toDto(Wallet wallet);

    @Mapping(source = "userId", target = "user.id")
    Wallet toEntity(WalletDto walletDto);
}
