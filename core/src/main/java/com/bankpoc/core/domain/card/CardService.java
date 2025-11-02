package com.bankpoc.core.domain.card;

import com.bankpoc.core.domain.account.Account;
import com.bankpoc.core.utils.CardNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {
    final CardRepository cardRepository;
    final CardNumberGenerator generator;
    @Transactional
    public Card newCard(Account account) {
        // --- LOGIC TO GENERATE A UNIQUE CARD NUMBER ---
        // In a real application, this would be a sophisticated sequence.
        // For a POC, we'll use a simplified unique number.
        String cardNumber = generateUniqueCardNumber();
        log.info("Generated new card number: {}", cardNumber);

        Card card = Card.builder()
                .account(account)
                .cardNumber(cardNumber)
                .expiryDate(java.time.LocalDate.now().plusYears(4)) // Example: 4 years validity
                .cardType(CardType.DEBIT) // Default to DEBIT
                .build();

        return cardRepository.save(card);
    }

    /**
     * Generates a unique, 16-digit, Luhn-compliant card number.
     * It ensures no duplicates exist in the database.
     * * @return A unique card number string.
     */
    private String generateUniqueCardNumber() {
        String baseNumber;
        String finalCardNumber;

        do {
            // 2. Use the utility to generate the random middle digits (9 digits)
            String randomDigits = generator.generateRandomDigits(CardNumberGenerator.RANDOM_DIGITS_LENGTH);

            // 3. Assemble the 15-digit base number (BIN + Random)
            baseNumber = CardNumberGenerator.DEFAULT_BIN + randomDigits;

            // 4. Use the utility to calculate the final 16th Luhn check digit
            int checkDigit = generator.calculateLuhnCheckDigit(baseNumber);

            // 5. Concatenate to get the final 16-digit card number
            finalCardNumber = (baseNumber + checkDigit).toString();

            // 6. Loop and regenerate if a duplicate is found in the database.
            // NOTE: This assumes cardRepository has a method 'existsByCardNumber'.
        } while (cardRepository.existsByCardNumber(finalCardNumber));

        return finalCardNumber;
    }

    public Optional<Card> getCardByAccount(Account account) {
        return cardRepository.findCardByAccount(account);
    }
}