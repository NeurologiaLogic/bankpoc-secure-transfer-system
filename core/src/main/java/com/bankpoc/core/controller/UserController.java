package com.bankpoc.core.controller;

import com.bankpoc.core.domain.jwt.UserDetails;
import com.bankpoc.core.domain.user.User;
import com.bankpoc.core.domain.user.UserService;
import com.bankpoc.core.dto.user.LoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("users")
@Validated
public class UserController {
    final UserService userService;
    final PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public Optional<User> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // 1. The @AuthenticationPrincipal annotation injects the UserDetails
        //    object that your JwtFilter and UserDetailsService created.

        // 2. Based on our previous fixes, userDetails.getUsername()
        //    will return the user's email.
        String email = userDetails.getUsername();

        // 3. Use your UserService to fetch the complete User object
        //    from the database and return it.
        return userService.findByEmail(email);
    }
}
