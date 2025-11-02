package com.bankpoc.core.domain.account;

import com.bankpoc.core.domain.card.Card;
import com.bankpoc.core.domain.card.CardService;
import com.bankpoc.core.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {
    final AccountRepository accountRepository;
    // Inject the CardService
    final CardService cardService;
    // Use SecureRandom for strong, cryptographically secure randomness
    private final SecureRandom secureRandom = new SecureRandom();
    private static final int ACCOUNT_NUMBER_LENGTH = 10;
    private static final long ACCOUNT_NUMBER_RANGE = 9000000000L;
    private static final long ACCOUNT_NUMBER_START = 1000000000L;
    @Transactional
    public Account newAccount(User user) {
        // 1. Create the Account
        String accountNumber = generateUniqueAccountNumber();

        Account newAccount = Account.builder()
                .accountNumber(accountNumber)
                .user(user)
                .balance(BigDecimal.ZERO) // Start with 0 balance
                .currency("IDR")
                .type("SAVINGS") // Example account type
                .status(AccountStatus.ACTIVE)
                .build();

        return accountRepository.save(newAccount);
    }

    // Simplified unique number generation (Must be robust in production)
    private String generateUniqueAccountNumber() {
        String accountNumber;

        do {
            // Generate a random number string of exactly 10 digits
            long number = ACCOUNT_NUMBER_START + (long)(secureRandom.nextDouble() * ACCOUNT_NUMBER_RANGE);
            accountNumber = String.valueOf(number);

            // Loop and regenerate if a duplicate is found in the database.
            // NOTE: This assumes accountRepository has a method 'existsByAccountNumber'.
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    public Optional<Account> getAccountByUser(User user) {
        return accountRepository.findAccountByUser(user);
    }
}