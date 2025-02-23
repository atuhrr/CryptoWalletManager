CREATE TABLE wallets (
                         id SERIAL PRIMARY KEY,
                         email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE assets (
                        id SERIAL PRIMARY KEY,
                        symbol VARCHAR(50) NOT NULL,
                        price NUMERIC(19,4) NOT NULL,
                        quantity NUMERIC(19,4) NOT NULL,
                        wallet_id INTEGER,
                        CONSTRAINT fk_wallet
                            FOREIGN KEY(wallet_id)
                                REFERENCES wallets(id)
                                ON DELETE CASCADE
);