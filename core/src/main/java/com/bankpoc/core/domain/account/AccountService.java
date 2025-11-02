package com.bankpoc.core.domain.account;

import com.bankpoc.core.domain.card.CardService;
import com.bankpoc.core.domain.user.User;
import com.bankpoc.core.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final CardService cardService;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final Duration ATTEMPT_EXPIRY = Duration.ofMinutes(15);
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(15);

    private final SecureRandom secureRandom = new SecureRandom();
    private static final long ACCOUNT_NUMBER_RANGE = 9000000000L;
    private static final long ACCOUNT_NUMBER_START = 1000000000L;

    // ===================== ACCOUNT CREATION =====================
    @Transactional
    public Account newAccount(User user) {
        try {
            String accountNumber = generateUniqueAccountNumber();

            Account newAccount = Account.builder()
                    .accountNumber(accountNumber)
                    .user(user)
                    .balance(BigDecimal.ZERO)
                    .currency("IDR")
                    .type("SAVINGS")
                    .status(AccountStatus.ACTIVE)
                    .build();

            Account saved = accountRepository.save(newAccount);
            log.info("✅ New account created: {} for user {}", saved.getAccountNumber(), user.getEmail());
            return saved;

        } catch (Exception e) {
            log.error("❌ Failed to create account for user {}: {}", user.getEmail(), e.getMessage(), e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create new account");
        }
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        try {
            do {
                long number = ACCOUNT_NUMBER_START + (long) (secureRandom.nextDouble() * ACCOUNT_NUMBER_RANGE);
                accountNumber = String.valueOf(number);
            } while (accountRepository.existsByAccountNumber(accountNumber));

            return accountNumber;
        } catch (Exception e) {
            log.error("❌ Error generating unique account number: {}", e.getMessage(), e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to generate account number");
        }
    }

    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        log.info("Fetching account by account number: {}", accountNumber);
        try {
            return accountRepository.findAccountByAccountNumber(accountNumber);
        } catch (Exception e) {
            log.error("❌ Failed to fetch account {}: {}", accountNumber, e.getMessage(), e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving account details");
        }
    }

    // ===================== PIN VERIFICATION =====================
    public boolean verifyPin(Account account, String pin) {
        String accountKey = "pin_attempts:" + account.getAccountNumber();
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        try {
            if (account.getCard() == null || account.getCard().getPinHash() == null) {
                log.warn("User tried PIN verification without setting up PIN for account {}", account.getAccountNumber());
                throw new ApiException(HttpStatus.BAD_REQUEST, "PIN not set. Please set up your card PIN first.");
            }

            if (isAccountBlocked(account)) {
                log.warn("Blocked account {} tried PIN verification", account.getAccountNumber());
                throw new ApiException(HttpStatus.FORBIDDEN, "Account temporarily blocked due to multiple failed attempts. Try again later.");
            }

            boolean pinMatched = passwordEncoder.matches(pin, account.getCard().getPinHash());

            if (!pinMatched) {
                long attempts = ops.increment(accountKey);
                if (attempts == 1) redisTemplate.expire(accountKey, ATTEMPT_EXPIRY);

                log.warn("⚠️ Failed PIN attempt {} for account {}", attempts, account.getAccountNumber());

                if (attempts >= MAX_FAILED_ATTEMPTS) {
                    blockAccount(account);
                    log.error("🚫 Account {} blocked due to multiple failed PIN attempts", account.getAccountNumber());
                    throw new ApiException(HttpStatus.FORBIDDEN, "Account blocked due to too many failed PIN attempts.");
                }

                throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid PIN. Please try again.");
            }

            // ✅ Successful login
            redisTemplate.delete(accountKey);
            log.info("✅ Successful PIN verification for account {}", account.getAccountNumber());
            return true;

        } catch (ApiException e) {
            log.error("API Exception during PIN verification for {}: {}", account.getAccountNumber(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ Unexpected error verifying PIN for {}: {}", account.getAccountNumber(), e.getMessage(), e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error verifying PIN");
        }
    }

    private boolean isAccountBlocked(Account account) {
        String blockedKey = "pin_blocked:" + account.getAccountNumber();
        return redisTemplate.hasKey(blockedKey);
    }

    private void blockAccount(Account account) {
        String blockedKey = "pin_blocked:" + account.getAccountNumber();
        redisTemplate.opsForValue().set(blockedKey, "1", BLOCK_DURATION);

        account.setStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);
        log.warn("Account {} marked as BLOCKED for {} minutes", account.getAccountNumber(), BLOCK_DURATION.toMinutes());
    }
}
