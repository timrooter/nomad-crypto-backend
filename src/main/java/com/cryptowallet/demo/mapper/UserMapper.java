package com.cryptowallet.demo.mapper;

import com.cryptowallet.demo.model.User;
import com.cryptowallet.demo.rest.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(UserDto dto);

    UserDto toDto(User entity);
}
