package com.bankpoc.core.domain.device;

import com.bankpoc.core.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "user_devices",
        // Enforce the unique constraint defined in SQL: UNIQUE(user_id, device_id)
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "device_id"})
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // --- Relationship to User (user_id) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // --- device_id (Unique per user) ---
    @Column(name = "device_id", length = 255, nullable = false)
    private String deviceId;

    // --- device_name ---
    @Column(name = "device_name", length = 100)
    private String deviceName;

    // --- ip_address ---
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    // --- is_trusted ---
    @Column(name = "is_trusted")
    private Boolean isTrusted; // Use Boolean wrapper for default value flexibility

    // --- Timestamps ---
    @Column(name = "first_login_at", updatable = false)
    private Instant firstLoginAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    // --- Lifecycle Hooks for Defaults ---

    @PrePersist
    protected void onCreate() {
        // Sets first_login_at default, matching SQL's CURRENT_TIMESTAMP
        this.firstLoginAt = Instant.now();

        // Sets is_trusted default, matching SQL's FALSE
        if (this.isTrusted == null) {
            this.isTrusted = false;
        }

        // Initialize last_login_at for consistency, though it will be updated later
        if (this.lastLoginAt == null) {
            this.lastLoginAt = this.firstLoginAt;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // Update the last_login_at whenever the entity is modified
        this.lastLoginAt = Instant.now();
    }
}