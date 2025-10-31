package com.bankpoc.core.controller;

import com.bankpoc.core.domain.user.User;
import com.bankpoc.core.domain.user.UserService;
import com.bankpoc.core.dto.user.UserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("user/v1")
@Validated
public class UserController {
    final UserService userService;
    final PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public String createUser(@Valid @RequestBody UserRequest userRequest) {
        String hashedPassword = passwordEncoder.encode(userRequest.getPassword());
        User user = userService.register(userRequest.getFullName(),hashedPassword, userRequest.getEmail(), userRequest.getPhoneNumber());
        return "User created successfully!";
    }



}
