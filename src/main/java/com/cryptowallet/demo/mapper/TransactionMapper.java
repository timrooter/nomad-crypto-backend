package com.cryptowallet.demo.mapper;

import com.cryptowallet.demo.model.Transaction;
import com.cryptowallet.demo.rest.dto.TransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(source = "toWalletId", target = "toUser.wallet.id")
    Transaction toEntity(TransactionDto dto);

    @Mapping(source = "fromUser.wallet.id", target = "fromWalletId")
    @Mapping(source = "toUser.wallet.id", target = "toWalletId")
    TransactionDto toDto(Transaction entity);
}
