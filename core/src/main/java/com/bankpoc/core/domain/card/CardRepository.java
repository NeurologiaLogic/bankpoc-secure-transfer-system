package com.bankpoc.core.domain.card;

import com.bankpoc.core.domain.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    public Boolean existsByCardNumber(String cardNumber);
    public Optional<Card> findCardByAccount(Account account);
}
