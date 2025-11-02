package com.bankpoc.core.domain.user;

import com.bankpoc.core.domain.account.Account;
import com.bankpoc.core.domain.account.AccountService;
import com.bankpoc.core.domain.card.CardService;
import com.bankpoc.core.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final CardService cardService;

    @Transactional
    public User register(String fullName, String passwordHash, String email, String phoneNumber) {

        User user = User.builder()
                .email(email)
                .passwordHash(passwordHash)
                .phoneNumber(phoneNumber)
                .fullName(fullName)
                .build();

        userRepository.save(user);

        Account account = accountService.newAccount(user);
        cardService.newCard(account);

        return user;

    }
    public Optional<User> existsByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findUserWithAccountAndCardByEmail(String email) {
        return userRepository.findUserWithAccountAndCardByEmail(email);
    }
}
