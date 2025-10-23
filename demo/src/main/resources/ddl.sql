drop table if exists bookrequest;
drop table if exists rentdetails;
drop table if exists details;
drop table if exists booklisting;
drop table if exists book;
drop table if exists users;


drop type if exists book_condition;
drop type if exists transaction_type;
drop type if exists request_status;

-- BOOK TABLE
CREATE TABLE book (
    id Serial PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
	author VARCHAR(100) NOT NULL,
	year INTEGER,
    genre VARCHAR(50)
);

-- USER TABLE
CREATE TABLE users (
    id Serial PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
	email VARCHAR(100) NOT NULL,
	password VARCHAR(255)
);

-- TYPES
CREATE TYPE book_condition AS ENUM ('NEW', 'USED');
CREATE TYPE transaction_type AS ENUM ('SELL', 'RENT', 'EXCHANGE', 'GIVEAWAY');
CREATE TYPE request_status AS ENUM ('PENDING', 'ACCEPTED', 'REJECTED');

-- BookListing TABLE
CREATE TABLE booklisting (
    id Serial PRIMARY KEY,
    book_id INTEGER NOT NULL,
	owner_id INTEGER NOT NULL,
	condition book_condition NOT NULL, -- VARCHAR(50)
	transaction_type transaction_type NOT NULL,
	status request_status NOT NULL,
    CONSTRAINT fk_booklisting_users FOREIGN KEY (owner_id) REFERENCES users(id),
	CONSTRAINT fk_booklisting_book FOREIGN KEY (book_id) REFERENCES book(id)
);

-- BookRequest TABLE
CREATE TABLE bookrequest (
	id Serial Primary Key,
    requester_id INTEGER NOT NULL,
    listing_id INTEGER NOT NULL,
	status request_status NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookrequest_users FOREIGN KEY (requester_id) REFERENCES users(id),
	CONSTRAINT fk_bookrequest_booklisting FOREIGN KEY (listing_id) REFERENCES booklisting(id) ON DELETE CASCADE
);

--BookListing Details (exists for sell and rent)
CREATE TABLE details(
	listing_id INTEGER PRIMARY KEY,
	price NUMERIC(10,2) NOT NULL,
	CONSTRAINT fk_details_booklisting FOREIGN KEY (listing_id) REFERENCES booklisting(id)
);

--BookListing Rent Details (exists for rent)
CREATE TABLE rentdetails(
	listing_id INTEGER NOT NULL,
	rental_start_date DATE, --this could be null until someone rents the book
	rental_duration Integer NOT NULL,
	CONSTRAINT fk_rentdetails_booklisting FOREIGN KEY (listing_id) REFERENCES details(listing_id)
);