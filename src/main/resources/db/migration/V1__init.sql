CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE plaid_items (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT REFERENCES users(id),
    access_token    VARCHAR(255) NOT NULL,
    item_id         VARCHAR(255) NOT NULL,
    institution     VARCHAR(255),
    created_at      TIMESTAMP DEFAULT NOW()
);

CREATE TABLE transactions (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT REFERENCES users(id),
    plaid_item_id   BIGINT REFERENCES plaid_items(id),
    plaid_txn_id    VARCHAR(255) UNIQUE NOT NULL,
    amount          DECIMAL(12,2) NOT NULL,
    category        VARCHAR(100),
    merchant        VARCHAR(255),
    txn_date        DATE NOT NULL,
    created_at      TIMESTAMP DEFAULT NOW()
);

CREATE TABLE budgets (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT REFERENCES users(id),
    category    VARCHAR(100) NOT NULL,
    amount      DECIMAL(12,2) NOT NULL,
    month       DATE NOT NULL,
    UNIQUE(user_id, category, month)
);