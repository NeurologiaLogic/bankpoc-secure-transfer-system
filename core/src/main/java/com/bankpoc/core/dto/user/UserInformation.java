package com.bankpoc.core.dto.user;


import com.bankpoc.core.domain.account.Account;
import com.bankpoc.core.domain.card.Card;
import com.bankpoc.core.domain.user.User;
import lombok.Builder;
import lombok.Data;

/**
 * DTO to represent an Account paired with its associated Card.
 * Assuming a 1:1 relationship between Account and Card.
 */
@Data
@Builder
public class UserInformation {
    private final Account account;
    private final Card card;
    private final User user;
}
