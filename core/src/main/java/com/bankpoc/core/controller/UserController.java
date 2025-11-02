package com.bankpoc.core.controller;

import com.bankpoc.core.domain.account.AccountService;
import com.bankpoc.core.domain.beneficiary.Beneficiary;
import com.bankpoc.core.domain.beneficiary.BeneficiaryService;
import com.bankpoc.core.domain.card.CardService;
import com.bankpoc.core.domain.jwt.UserDetails;
import com.bankpoc.core.domain.user.User;
import com.bankpoc.core.domain.user.UserService;
import com.bankpoc.core.dto.beneficiary.BeneficiaryRequest;
import com.bankpoc.core.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
@Validated
@Slf4j
public class UserController {

    private final UserService userService;
    private final AccountService accountService;
    private final BeneficiaryService beneficiaryService;
    private final CardService cardService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        log.info("Fetching profile for user: {}", email);

        try {
            Optional<User> userOptional = userService.findUserWithAccountAndCardByEmail(email);

            if (userOptional.isEmpty()) {
                log.warn("User profile not found for authenticated email: {}", email);
                throw new ApiException(HttpStatus.NOT_FOUND, "User profile not found");
            }

            log.info("Profile retrieved successfully for user: {}", email);
            return ResponseEntity.ok(userOptional.get());

        } catch (ApiException e) {
            log.error("API error fetching profile for {}: {}", email, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error fetching profile for {}: {}", email, e.getMessage(), e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch user profile");
        }
    }

    @GetMapping("/me/beneficiary")
    public ResponseEntity<List<Beneficiary>> getAllBeneficiaries(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String bankCode) {

        String email = userDetails.getUsername();
        log.info("Fetching beneficiaries for user: {}, bankCode filter: {}", email, bankCode);

        try {
            Optional<List<Beneficiary>> beneficiaries =
                    (bankCode != null)
                            ? beneficiaryService.getByUserEmailAndBankCode(email, bankCode)
                            : beneficiaryService.getByUserEmail(email);

            if (beneficiaries.isEmpty() || beneficiaries.get().isEmpty()) {
                log.warn("No beneficiaries found for user: {}", email);
                throw new ApiException(HttpStatus.NOT_FOUND, "No beneficiaries found");
            }

            log.info("Beneficiaries retrieved successfully for user: {}", email);
            return ResponseEntity.ok(beneficiaries.get());

        } catch (ApiException e) {
            log.error("API error fetching beneficiaries for {}: {}", email, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error fetching beneficiaries for {}: {}", email, e.getMessage(), e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch beneficiaries");
        }
    }

    @PostMapping("/me/beneficiary")
    public ResponseEntity<Beneficiary> addBeneficiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody BeneficiaryRequest request) {

        String email = userDetails.getUsername();
        log.info("Adding beneficiary for user: {}, request: {}", email, request);

        try {
            Optional<Beneficiary> saved = beneficiaryService.addBeneficiary(email, request);

            if (saved.isEmpty()) {
                log.warn("Failed to add beneficiary for user: {}", email);
                throw new ApiException(HttpStatus.BAD_REQUEST, "Failed to add beneficiary");
            }

            log.info("Beneficiary added successfully for user: {}", email);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved.get());

        } catch (ApiException e) {
            log.error("API error adding beneficiary for {}: {}", email, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error adding beneficiary for {}: {}", email, e.getMessage(), e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error adding beneficiary");
        }
    }
}
