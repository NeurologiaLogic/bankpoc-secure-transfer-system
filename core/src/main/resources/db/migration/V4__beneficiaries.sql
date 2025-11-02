-- ============= BENEFICIARIES =============
CREATE TABLE beneficiaries (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID REFERENCES users(id) ON DELETE CASCADE,  -- the owner (who saves the beneficiary)
    name                VARCHAR(100) NOT NULL,                        -- beneficiary name
    account_number      VARCHAR(20) NOT NULL,                         -- destination account
    bank_code           VARCHAR(20) DEFAULT 'BANKPOC',                -- e.g., BCA, MANDIRI, or BANKPOC
    bank_name           VARCHAR(100) DEFAULT 'Bank POC',
    alias               VARCHAR(100),                                 -- optional nickname (e.g. "Mom", "Business A")
    is_internal         BOOLEAN DEFAULT TRUE,                         -- internal (same system) or external bank
    is_verified         BOOLEAN DEFAULT FALSE,                        -- true if confirmed via OTP
    verified_at         TIMESTAMP,
    status              VARCHAR(20) DEFAULT 'ACTIVE',                 -- ACTIVE, INACTIVE, BLOCKED
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, account_number, bank_code)
);

CREATE INDEX idx_beneficiaries_user_id ON beneficiaries(user_id);

