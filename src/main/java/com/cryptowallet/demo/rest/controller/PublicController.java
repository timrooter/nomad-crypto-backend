package com.cryptowallet.demo.rest.controller;

import com.cryptowallet.demo.service.TransactionService;
import com.cryptowallet.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/public")
public class PublicController {

    private final UserService userService;

    @GetMapping("/numberOfUsers")
    public Integer getNumberOfUsers() {
        return userService.getAllUsers().size();
    }

}
