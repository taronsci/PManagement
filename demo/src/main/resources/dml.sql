-- delete from rentdetails
-- delete from details
-- delete from bookrequest
-- delete from booklisting
-- delete from book
-- delete from users


INSERT INTO book (title, author, year, genre) VALUES
('1984', 'George Orwell', 1949, 'Dystopian'),
('The Hobbit', 'J.R.R. Tolkien', 1937, 'Fantasy'),
('Brave New World', 'Aldous Huxley', 1932, 'Science Fiction');

INSERT INTO users (username, email, password) VALUES
('alice', 'alice@example.com', 'password123'),
('bob', 'bob@example.com', 'password456'),
('charlie', 'charlie@example.com', 'password789');

INSERT INTO booklisting (book_id, owner_id, condition, transaction_type, status) VALUES
(1, 1, 'USED', 'SELL', 'PENDING'),
(2, 2, 'NEW', 'GIVEAWAY', 'PENDING'),
(3, 3, 'USED', 'RENT', 'PENDING');

INSERT INTO details (listing_id, price) VALUES
(1, 10.00),  -- 1984 for sale
(3, 5.00);   -- Brave New World for rent

INSERT INTO rentdetails (listing_id, rental_start_date, rental_duration) VALUES
(3, '2025-09-18', 14);  -- Brave New World rent details

INSERT INTO bookrequest (requester_id, listing_id, status) VALUES
(2, 1, 'PENDING'),  -- Bob requests 1984
(1, 2, 'PENDING');  -- Alice requests The Hobbit

