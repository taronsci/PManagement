drop table if exists users;

CREATE TABLE tokens (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id INTEGER NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES "users"(id) ON DELETE CASCADE
);

CREATE TABLE users (
    id Serial PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL,
	email VARCHAR(100) NOT NULL,
	password VARCHAR(255),
	enabled BOOL NOT NULL DEFAULT FALSE
);

-- TYPES
CREATE TYPE item_condition AS ENUM ('LOST', 'FOUND');
