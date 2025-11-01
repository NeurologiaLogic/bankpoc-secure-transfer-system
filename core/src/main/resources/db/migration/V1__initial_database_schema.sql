-- ============= USERS =============
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name           VARCHAR(100) NOT NULL,
    email               VARCHAR(100) UNIQUE NOT NULL,
    password_hash       VARCHAR(255) NOT NULL,
    phone_number        VARCHAR(30) UNIQUE,
    national_id_number  VARCHAR(50),
    kyc_status          VARCHAR(20) DEFAULT 'PENDING',
    status              VARCHAR(20) DEFAULT 'ACTIVE',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============= ACCOUNTS =============
CREATE TABLE accounts (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_number      VARCHAR(20) UNIQUE NOT NULL,
    user_id             UUID REFERENCES users(id),
    balance             NUMERIC(18,2) DEFAULT 0.00,
    currency            VARCHAR(10) DEFAULT 'IDR',
    type                VARCHAR(20) DEFAULT 'SAVINGS',
    status              VARCHAR(20) DEFAULT 'ACTIVE',
    version             BIGINT DEFAULT 0,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_accounts_user_id ON accounts(user_id);

-- ============= CARDS =============
CREATE TABLE cards (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id          UUID REFERENCES accounts(id),
    card_number         VARCHAR(16) UNIQUE NOT NULL,
    card_type           VARCHAR(20) DEFAULT 'VIRTUAL',
    status              VARCHAR(20) DEFAULT 'ACTIVE',
    expiry_date         DATE NOT NULL,
    pin_hash            VARCHAR(255) NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_cards_account_id ON cards(account_id);

-- ============= TRANSACTIONS =============
CREATE TABLE transactions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_account_id     UUID REFERENCES accounts(id),
    to_account_id       UUID REFERENCES accounts(id),
    amount              NUMERIC(18,2) NOT NULL CHECK (amount > 0),
    currency            VARCHAR(10) DEFAULT 'IDR',
    type                VARCHAR(20) NOT NULL, -- DEPOSIT, WITHDRAWAL, TRANSFER
    status              VARCHAR(20) DEFAULT 'PENDING',
    description         TEXT,
    reference_id        VARCHAR(50),
    idempotency_key     VARCHAR(100),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at        TIMESTAMP
);
CREATE INDEX idx_transactions_accounts ON transactions(from_account_id, to_account_id);

-- ============= OTP REQUESTS =============
CREATE TABLE otp_requests (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID REFERENCES users(id),
    otp_code            VARCHAR(6) NOT NULL,
    purpose             VARCHAR(30) NOT NULL,
    expires_at          TIMESTAMP NOT NULL,
    consumed            BOOLEAN DEFAULT FALSE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_otp_user_id ON otp_requests(user_id);

-- ============= AUDIT LOGS =============
CREATE TABLE audit_logs (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID,
    action              VARCHAR(100) NOT NULL,
    details             JSONB,
    ip_address          VARCHAR(45),
    user_agent          VARCHAR(255),
    correlation_id      UUID,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============= IDEMPOTENCY KEYS =============
CREATE TABLE idempotency_keys (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key_value           VARCHAR(100) UNIQUE NOT NULL,
    response_hash       VARCHAR(255),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at          TIMESTAMP
);

CREATE TABLE account_ledgers (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id      UUID REFERENCES transactions(id),
    account_id          UUID REFERENCES accounts(id),
    entry_type          VARCHAR(10) CHECK (entry_type IN ('DEBIT', 'CREDIT')),
    amount              NUMERIC(18,2) NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
