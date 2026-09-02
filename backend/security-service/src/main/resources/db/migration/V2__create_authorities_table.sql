CREATE TABLE user_authorities (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES user_accounts(uuid) ON DELETE CASCADE,
    authority VARCHAR(100) NOT NULL CHECK (authority <> ''),
    version BIGINT NOT NULL,

    UNIQUE (user_id, authority)
);