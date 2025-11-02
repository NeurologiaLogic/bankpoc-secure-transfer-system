package com.bankpoc.core.controller;

import com.bankpoc.core.domain.account.Account;
import com.bankpoc.core.domain.account.AccountService;
import com.bankpoc.core.domain.card.Card;
import com.bankpoc.core.domain.card.CardService;
import com.bankpoc.core.domain.jwt.UserDetails;
import com.bankpoc.core.domain.user.User;
import com.bankpoc.core.domain.user.UserService;
import com.bankpoc.core.dto.user.UserInformation;
import com.bankpoc.core.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("users")
@Validated
@Slf4j
public class UserController {
    final UserService userService;
    final AccountService accountService;
    final CardService cardService;
    final PasswordEncoder passwordEncoder;

//    @GetMapping("/me")
//    public ResponseEntity<UserInformation> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
//        String email = userDetails.getUsername();
//        Optional<User> userOptional = userService.findByEmail(email);
//        if (userOptional.isEmpty()) {
//            log.warn("User not found for authenticated email: {}", email);
//            throw new UsernameNotFoundException("User not found for authenticated email: " + email);
//        }
//
//        User user = userOptional.get();
//
//        // 2. Fetch Account (Optional)
//        // We chain the calls safely using Optional.flatMap for better performance and readability
//        Optional<Account> accountOptional = accountService.getAccountByUser(user);
//
//        // 3. Fetch Card based on Account (Optional)
//        // flatMap allows us to chain Optional calls without checking for isPresent()
//        Optional<Card> cardOptional = accountOptional
//                .flatMap(account -> cardService.getCardByAccount(account));
//
//        // 4. Extract data and build the DTO
//        UserInformation userInformation = UserInformation.builder()
//                .user(user)
//                .account(accountOptional.orElse(null)) // Pass Account if present, otherwise null
//                .card(cardOptional.orElse(null))       // Pass Card if present, otherwise null
//                .build();
//
//        // 5. Return 200 OK with the consolidated DTO
//        return ResponseEntity.ok(userInformation);
//    }
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        // **EFFICIENT FETCHING:**
        // Instead of fetching User, then Account, then Card in separate queries (N+2 problem),
        // we assume a new UserService method exists that uses a JOIN FETCH query
        // to retrieve the User, Account, and Card in a single database round trip.
        // This is the most efficient approach.

        Optional<User> userOptional = userService.findUserWithAccountAndCardByEmail(email);

        if (userOptional.isEmpty()) {
            log.warn("User profile not found for authenticated email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Return 200 OK with the consolidated DTO
        return ResponseEntity.ok(userOptional.get());
    }
}
