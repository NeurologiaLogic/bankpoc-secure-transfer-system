-- V2: Add Device Trust & KYC Flow Adjustments

-- 1. Make card PIN nullable to allow for deferred PIN setup
ALTER TABLE cards
ALTER COLUMN pin_hash DROP NOT NULL;

-- 2. Change default user status to 'INACTIVE' pending KYC
ALTER TABLE users
ALTER COLUMN status SET DEFAULT 'INACTIVE';

-- 3. Change default account status to 'INACTIVE' pending KYC
ALTER TABLE accounts
ALTER COLUMN status SET DEFAULT 'INACTIVE';

-- 4. Create new table to track user devices and trust status
CREATE TABLE user_devices (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID REFERENCES users(id) ON DELETE CASCADE,
    device_id           VARCHAR(255) NOT NULL, -- A hash of User-Agent or a client-generated UUID
    device_name         VARCHAR(100), -- e.g., "Patrick's iPhone 15"
    ip_address          VARCHAR(45),
    is_trusted          BOOLEAN DEFAULT FALSE,
    first_login_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at       TIMESTAMP,

    -- A user can have many devices, but each device_id per user must be unique
    UNIQUE(user_id, device_id)
);

CREATE INDEX idx_user_devices_user_id ON user_devices(user_id);