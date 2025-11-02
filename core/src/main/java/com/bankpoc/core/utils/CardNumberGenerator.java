package com.bankpoc.core.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CardNumberGenerator {

    // Standard 16-digit card length
    public static final int CARD_NUMBER_LENGTH = 16;

    // Bank Identification Number (BIN) or Issuer Identification Number (IIN)
    public static final String DEFAULT_BIN = "456789";

    // The length of the random segment required (16 total - 6 BIN - 1 Luhn Check = 9)
    public static final int RANDOM_DIGITS_LENGTH = CARD_NUMBER_LENGTH - DEFAULT_BIN.length() - 1;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a string of cryptographically strong random digits.
     * @param length The desired length of the digit string.
     * @return A string of random digits.
     */
    public String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Calculates the Luhn check digit for the given number string (ISO/IEC 7812).
     * @param number The number string (15 digits) before the check digit.
     * @return The calculated check digit (0-9).
     */
    public int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean alternate = true;

        // Iterate backwards from the last digit of the base number
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));

            if (alternate) {
                // Double every second digit
                digit *= 2;
                // If doubled digit is > 9, subtract 9
                if (digit > 9) {
                    digit = digit - 9;
                }
            }
            sum += digit;
            alternate = !alternate; // Toggle state
        }

        // The check digit is the number needed to make the total sum a multiple of 10
        int checkDigit = (sum * 9) % 10;
        return checkDigit;
    }
}
