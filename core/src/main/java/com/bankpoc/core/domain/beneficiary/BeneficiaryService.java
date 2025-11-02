package com.bankpoc.core.domain.beneficiary;

import com.bankpoc.core.constant.BankCode;
import com.bankpoc.core.domain.account.Account;
import com.bankpoc.core.domain.account.AccountService;
import com.bankpoc.core.dto.beneficiary.BeneficiaryRequest;
import com.bankpoc.core.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final AccountService accountService;

    /**
     * Get all beneficiaries for a user by email.
     */
    public Optional<List<Beneficiary>> getByUserEmail(String email) {
        List<Beneficiary> list = beneficiaryRepository.findByUserEmail(email);
        if (list.isEmpty()) {
            throw ApiException.notFound( "No beneficiaries found for user: " + email);
        }
        return Optional.of(list);
    }

    /**
     * Get all beneficiaries for a user filtered by bankCode.
     */
    public Optional<List<Beneficiary>> getByUserEmailAndBankCode(String email, String bankCode) {
        BankCode parsedBankCode;
        try {
            parsedBankCode = BankCode.fromString(bankCode);
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("Invalid bank code: " + bankCode);
        }

        List<Beneficiary> list = beneficiaryRepository.findByUserEmailAndBankCode(email, parsedBankCode);
        if (list.isEmpty()) {
            throw ApiException.notFound(String.format("No beneficiaries found for user %s under bank %s", email, bankCode));
        }

        return Optional.of(list);
    }

    /**
     * Add a new beneficiary for the given user.
     */
    public Optional<Beneficiary> addBeneficiary(String email, BeneficiaryRequest request) {
        Optional<Account> accountOptional = accountService.getAccountByAccountNumber(request.getAccountNumber());

        if (accountOptional.isEmpty()) {
            log.warn("Cannot add beneficiary, account not found for email: {}", email);
            throw ApiException.badRequest("Account not found for given account number");
        }

        Account account = accountOptional.get();

        // Verify PIN or throw ApiException if invalid
        try {
            accountService.verifyPin(account, request.getPin());
        } catch (ApiException ex) {
            throw ex; // Already an ApiException, let it propagate
        } catch (Exception ex) {
            throw ApiException.unauthorized("Invalid PIN provided");
        }

        // Restrict to internal banks for now
        BankCode bankCode = BankCode.BANKPOC;
        String bankName = bankCode.getFullName();

        Beneficiary beneficiary = Beneficiary.builder()
                .user(account.getUser())
                .name(account.getUser().getFullName())
                .accountNumber(request.getAccountNumber())
                .bankCode(bankCode)
                .bankName(bankName)
                .isInternal(true)
                .status("ACTIVE")
                .createdAt(Instant.now())
                .build();

        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        log.info("Beneficiary added successfully for user: {}", email);

        return Optional.of(saved);
    }
}
