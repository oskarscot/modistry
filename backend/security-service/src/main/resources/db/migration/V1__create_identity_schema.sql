CREATE TABLE user_accounts (
    uuid UUID PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(320),
    avatar_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL
);

CREATE TABLE linked_accounts (
    id UUID PRIMARY KEY,
    provider_name VARCHAR(64) NOT NULL,
    provider_sub VARCHAR(255) NOT NULL,
    user_account_id UUID NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT fk_linked_accounts_user_account
        FOREIGN KEY (user_account_id)
        REFERENCES user_accounts (uuid)
        ON DELETE CASCADE,
    CONSTRAINT uq_linked_accounts_provider_subject
        UNIQUE (provider_name, provider_sub)
);

CREATE INDEX idx_linked_accounts_user_account_id
    ON linked_accounts (user_account_id);
