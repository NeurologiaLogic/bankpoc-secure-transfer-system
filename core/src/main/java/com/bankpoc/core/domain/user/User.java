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
    String status;
    String nationalIdNumber;
    Instant createdAt;
    Instant updatedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
