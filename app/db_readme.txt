-- accounts table
CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    account_name VARCHAR(255) NOT NULL
);

-- cards table
CREATE TABLE cards (
    id SERIAL PRIMARY KEY,
    card_number VARCHAR(16) NOT NULL,
    account_id INT REFERENCES accounts(id) ON DELETE CASCADE,
    balance DECIMAL(10, 2) DEFAULT 0
);

-- transactions table
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    card_id INT REFERENCES cards(id) ON DELETE CASCADE,
    target_card_id INT REFERENCES cards(id) ON DELETE CASCADE,
    transaction_amount DECIMAL(10, 2) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,  -- Stores transaction type directly (Credit, Debit, Transfer)
    description VARCHAR(255),
    bad_data BOOLEAN DEFAULT FALSE,  -- Flags bad transactions
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
