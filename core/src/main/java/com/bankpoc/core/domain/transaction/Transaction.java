package com.bankpoc.core.domain.transaction;


import com.bankpoc.core.domain.account.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // --- Relationships ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    // --- Data Fields ---

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(length = 10)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type; // Assumes you create enum TransactionType

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TransactionType status; // Assumes you create enum TransactionStatus

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String referenceId;

    @Column(length = 100)
    private String idempotencyKey;

    // --- Timestamps ---

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant completedAt;


    // --- Lifecycle Hooks for Defaults ---

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();

        // Set defaults from your SQL schema
        if (this.status == null) {
            this.status = TransactionType.PENDING;
        }
        if (this.currency == null) {
            this.currency = "IDR";
        }
    }
}