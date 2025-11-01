package com.bankpoc.core.domain.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    String fullName;
    String passwordHash;
    String email;
    String phoneNumber;

    @Enumerated(EnumType.STRING)
    KycStatus kycStatus;

    @Enumerated(EnumType.STRING)
    UserStatus status;
    String nationalIdNumber;
    Instant createdAt;
    Instant updatedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now; // Set updatedAt on creation as well

        // Set defaults only if they are not provided
        if (this.kycStatus == null) {
            // Assuming you have an enum value like NOT_STARTED
            this.kycStatus = KycStatus.PENDING;
        }
        if (this.status == null) {
            // Example: default status
            this.status = UserStatus.INACTIVE;
        }
    }
}
