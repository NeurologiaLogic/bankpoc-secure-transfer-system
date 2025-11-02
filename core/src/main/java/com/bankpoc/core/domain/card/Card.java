package com.bankpoc.core.domain.card;

import com.bankpoc.core.domain.account.Account;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "card_number", length = 16, unique = true, nullable = false)
    private String cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", length = 20)
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CardStatus status;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "pin_hash", length = 255, nullable = true)
    private String pinHash;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();

        // Set defaults from your SQL schema
        if (this.cardType == null) {
            this.cardType = CardType.DEBIT;
        }

        if (this.status == null) {
            this.status = CardStatus.INACTIVE;
        }
    }
}
