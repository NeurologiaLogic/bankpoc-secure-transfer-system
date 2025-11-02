package com.bankpoc.core.domain.beneficiary;

import com.bankpoc.core.constant.BankCode;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import com.bankpoc.core.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "beneficiaries",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "account_number", "bank_code"})
        },
        indexes = {
                @Index(name = "idx_beneficiaries_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Owner (who saved this beneficiary)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The beneficiary’s name (account holder)
    @Column(nullable = false, length = 100)
    private String name;

    // Destination account info
    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;

    @Column(name = "bank_code", length = 20)
    @Enumerated(EnumType.STRING)
    private BankCode bankCode;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    // Optional nickname like “Mom” or “Business A”
    @Column(length = 100)
    private String alias;

    // True = internal bank account (our system), False = external bank
    @Column(name = "is_internal")
    private Boolean isInternal = true;

    // Verification
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    // ACTIVE / INACTIVE / BLOCKED
    @Column(length = 20)
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist()
    {
        createdAt = Instant.now();
        if(bankCode == null){
            BankCode defaultBank = BankCode.BANKPOC;
            this.bankCode = defaultBank;
            this.bankName = defaultBank.getFullName();
        }

    }
}

