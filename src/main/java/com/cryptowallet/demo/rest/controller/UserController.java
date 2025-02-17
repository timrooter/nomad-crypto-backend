package com.cryptowallet.demo.rest.controller;

import com.cryptowallet.demo.mapper.UserMapper;
import com.cryptowallet.demo.mapper.WalletMapper;
import com.cryptowallet.demo.model.User;
import com.cryptowallet.demo.model.Wallet;
import com.cryptowallet.demo.rest.dto.UserDto;
import com.cryptowallet.demo.rest.dto.WalletDto;
import com.cryptowallet.demo.service.UserService;
import com.cryptowallet.demo.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return users.stream()
                    .map(UserMapper.INSTANCE::toDto)
                    .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return UserMapper.INSTANCE.toDto(user);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        User user = UserMapper.INSTANCE.toEntity(userDto);
        user = userService.updateUser(id, user);
        return UserMapper.INSTANCE.toDto(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/wallet/{username}")
    public WalletDto getWalletByUsername(@PathVariable String username) {
        User user = userService.validateAndGetUserByUsername(username);
        Wallet wallet = walletService.getWalletByUserId(user.getId());
        return WalletMapper.INSTANCE.toDto(wallet);
    }

}
