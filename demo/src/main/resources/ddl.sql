drop table if exists users;


-- USER TABLE
CREATE TABLE users (
    id Serial PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL,
	email VARCHAR(100) NOT NULL,
	password VARCHAR(255)
);

-- TYPES
CREATE TYPE item_condition AS ENUM ('LOST', 'FOUND');
