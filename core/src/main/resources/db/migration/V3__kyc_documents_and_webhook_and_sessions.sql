-- ============= KYC RECORDS =============
CREATE TABLE kyc_records (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID REFERENCES users(id) ON DELETE CASCADE,
    kyc_level           VARCHAR(20) DEFAULT 'BASIC', -- BASIC / ADVANCED / PREMIUM
    status              VARCHAR(20) DEFAULT 'PENDING', -- PENDING / APPROVED / REJECTED
    rejection_reason    TEXT,
    submitted_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at         TIMESTAMP,
    reviewed_by         UUID, -- Admin user ID (if you add admin table)
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_kyc_user_id ON kyc_records(user_id);

-- ============= KYC DOCUMENTS =============
CREATE TABLE kyc_documents (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    kyc_record_id       UUID REFERENCES kyc_records(id) ON DELETE CASCADE,
    document_type       VARCHAR(50) NOT NULL, -- e.g., 'NATIONAL_ID', 'SELFIE', 'PROOF_OF_ADDRESS'
    document_url        TEXT NOT NULL,        -- S3 or storage path
    verified            BOOLEAN DEFAULT FALSE,
    verified_at         TIMESTAMP,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_kyc_documents_record_id ON kyc_documents(kyc_record_id);

--Later
--ALTER TABLE users
--ADD COLUMN latest_kyc_id UUID REFERENCES kyc_records(id);


-- ============= Multiple Login Sessions =============
CREATE TABLE user_sessions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID REFERENCES users(id) ON DELETE CASCADE,
    refresh_token       VARCHAR(255) UNIQUE NOT NULL,
    device_id           UUID REFERENCES user_devices(id),
    ip_address          VARCHAR(45),
    expires_at          TIMESTAMP NOT NULL,
    revoked             BOOLEAN DEFAULT FALSE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_user_sessions_user_id ON user_sessions(user_id);

-- ============= Webhooks and Events =============
CREATE TABLE event_outbox (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type          VARCHAR(100) NOT NULL,
    payload             JSONB NOT NULL,
    processed           BOOLEAN DEFAULT FALSE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at        TIMESTAMP
);
CREATE INDEX idx_event_outbox_processed ON event_outbox(processed);

