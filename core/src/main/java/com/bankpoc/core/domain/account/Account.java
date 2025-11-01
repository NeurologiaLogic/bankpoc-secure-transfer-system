package com.bankpoc.core.domain.account;

import com.bankpoc.core.domain.transaction.TransactionType;
import com.bankpoc.core.domain.user.User;
import com.bankpoc.core.domain.user.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal; // <-- FIX: Import BigDecimal
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User userId;

    // --- FIX 1: Use BigDecimal for NUMERIC/currency ---
    BigDecimal balance;

    String currency;
    String type;

    @Enumerated(EnumType.STRING)
    AccountStatus status;

    // --- FIX 2: Use Long for BIGINT ---
    @Version // <-- Good practice to add @Version for optimistic locking
    Long version;

    Instant createdAt;
    Instant updatedAt;


    // --- Lifecycle Hooks for Defaults ---

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();

        if(this.balance==null){
            // --- FIX 1: Use BigDecimal.ZERO ---
            this.balance = BigDecimal.ZERO;
        }

        if(this.version==null){
            // --- FIX 2: Use 0L for Long ---
            this.version = 0L;
        }

        if (this.currency == null) {
            this.currency = "IDR";
        }

        if (this.status == null) {
            this.status = AccountStatus.INACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
