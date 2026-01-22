-- ============================================
-- Library Management System - Sample Data
-- This file runs on every application startup
-- Uses conditional inserts to avoid conflicts
-- ============================================

-- ========== AUTHORS ==========
-- Only insert if author with this email doesn't exist
INSERT INTO author (author_name, email, public_id)
SELECT 'J.K. Rowling', 'jk.rowling@authors.com', 'AUTH-JKROWLING'
WHERE NOT EXISTS (SELECT 1 FROM author WHERE email = 'jk.rowling@authors.com');

INSERT INTO author (author_name, email, public_id)
SELECT 'George Orwell', 'george.orwell@authors.com', 'AUTH-GEORGEORWELL'
WHERE NOT EXISTS (SELECT 1 FROM author WHERE email = 'george.orwell@authors.com');

INSERT INTO author (author_name, email, public_id)
SELECT 'Isaac Asimov', 'isaac.asimov@authors.com', 'AUTH-ISAACASIMOV'
WHERE NOT EXISTS (SELECT 1 FROM author WHERE email = 'isaac.asimov@authors.com');

INSERT INTO author (author_name, email, public_id)
SELECT 'Agatha Christie', 'agatha.christie@authors.com', 'AUTH-AGATHACHRISTIE'
WHERE NOT EXISTS (SELECT 1 FROM author WHERE email = 'agatha.christie@authors.com');

INSERT INTO author (author_name, email, public_id)
SELECT 'Stephen King', 'stephen.king@authors.com', 'AUTH-STEPHENKING'
WHERE NOT EXISTS (SELECT 1 FROM author WHERE email = 'stephen.king@authors.com');

INSERT INTO author (author_name, email, public_id)
SELECT 'Arthur Conan Doyle', 'arthur.doyle@authors.com', 'AUTH-ARTHURDOYLE'
WHERE NOT EXISTS (SELECT 1 FROM author WHERE email = 'arthur.doyle@authors.com');

INSERT INTO author (author_name, email, public_id)
SELECT 'Jules Verne', 'jules.verne@authors.com', 'AUTH-JULESVERNE'
WHERE NOT EXISTS (SELECT 1 FROM author WHERE email = 'jules.verne@authors.com');

INSERT INTO author (author_name, email, public_id)
SELECT 'H.G. Wells', 'hg.wells@authors.com', 'AUTH-HGWELLS'
WHERE NOT EXISTS (SELECT 1 FROM author WHERE email = 'hg.wells@authors.com');

-- ========== USERS (Admin, Staff, Regular Users) ==========
-- Password for all sample users: 'password123' (BCrypt encoded)
-- $2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6

-- Admin User
INSERT INTO users (version, public_id, customer_name, email, country_code, mobile_number, address, date_of_birth, password_hash, secret_question, secret_answer_hash, is_defaulter, total_unpaid_fine)
SELECT 0, 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Admin User', 'admin@library.com', '+91', '9876543210', '123 Admin Street, City', '1985-01-15', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'What is your pet name?', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', false, 0.00
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@library.com');

-- Staff User
INSERT INTO users (version, public_id, customer_name, email, country_code, mobile_number, address, date_of_birth, password_hash, secret_question, secret_answer_hash, is_defaulter, total_unpaid_fine)
SELECT 0, 'b2c3d4e5-f6a7-8901-bcde-f23456789012', 'Staff Member', 'staff@library.com', '+91', '9876543211', '456 Staff Avenue, City', '1990-06-20', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'What is your pet name?', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', false, 0.00
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'staff@library.com');

-- Regular Users
INSERT INTO users (version, public_id, customer_name, email, country_code, mobile_number, address, date_of_birth, password_hash, secret_question, secret_answer_hash, is_defaulter, total_unpaid_fine)
SELECT 0, 'c3d4e5f6-a7b8-9012-cdef-345678901234', 'John Doe', 'john.doe@email.com', '+91', '9876543212', '789 Reader Lane, City', '1995-03-10', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'What is your pet name?', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', false, 0.00
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'john.doe@email.com');

INSERT INTO users (version, public_id, customer_name, email, country_code, mobile_number, address, date_of_birth, password_hash, secret_question, secret_answer_hash, is_defaulter, total_unpaid_fine)
SELECT 0, 'd4e5f6a7-b8c9-0123-def0-456789012345', 'Jane Smith', 'jane.smith@email.com', '+91', '9876543213', '101 Book Street, City', '1992-11-25', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'What is your pet name?', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', false, 0.00
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'jane.smith@email.com');

INSERT INTO users (version, public_id, customer_name, email, country_code, mobile_number, address, date_of_birth, password_hash, secret_question, secret_answer_hash, is_defaulter, total_unpaid_fine)
SELECT 0, 'e5f6a7b8-c9d0-1234-ef01-567890123456', 'Bob Wilson', 'bob.wilson@email.com', '+91', '9876543214', '202 Library Road, City', '1988-07-08', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'What is your pet name?', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', true, 50.00
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'bob.wilson@email.com');

INSERT INTO users (version, public_id, customer_name, email, country_code, mobile_number, address, date_of_birth, password_hash, secret_question, secret_answer_hash, is_defaulter, total_unpaid_fine)
SELECT 0, 'f6a7b8c9-d0e1-2345-f012-678901234567', 'Alice Brown', 'alice.brown@email.com', '+91', '9876543215', '303 Novel Avenue, City', '1997-09-12', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'What is your pet name?', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', false, 0.00
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice.brown@email.com');

-- ========== USER ROLES ==========
-- Add ADMIN role to admin user
INSERT INTO user_roles (users_id, role)
SELECT u.id, 'ADMIN' FROM users u WHERE u.email = 'admin@library.com'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.users_id = u.id AND ur.role = 'ADMIN');

-- Add STAFF role to staff user
INSERT INTO user_roles (users_id, role)
SELECT u.id, 'STAFF' FROM users u WHERE u.email = 'staff@library.com'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.users_id = u.id AND ur.role = 'STAFF');

-- Add USER role to regular users
INSERT INTO user_roles (users_id, role)
SELECT u.id, 'USER' FROM users u WHERE u.email = 'john.doe@email.com'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.users_id = u.id AND ur.role = 'USER');

INSERT INTO user_roles (users_id, role)
SELECT u.id, 'USER' FROM users u WHERE u.email = 'jane.smith@email.com'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.users_id = u.id AND ur.role = 'USER');

INSERT INTO user_roles (users_id, role)
SELECT u.id, 'USER' FROM users u WHERE u.email = 'bob.wilson@email.com'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.users_id = u.id AND ur.role = 'USER');

INSERT INTO user_roles (users_id, role)
SELECT u.id, 'USER' FROM users u WHERE u.email = 'alice.brown@email.com'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.users_id = u.id AND ur.role = 'USER');

-- ========== BOOKS ==========
INSERT INTO book (version, public_id, book_title, category, cover_url, description, total_copies)
SELECT 0, 'HP001', 'Harry Potter and the Philosopher''s Stone', 'FICTION', 'https://covers.openlibrary.org/b/id/10110415-L.jpg', 'The first book in the Harry Potter series following the adventures of a young wizard.', 5
WHERE NOT EXISTS (SELECT 1 FROM book WHERE public_id = 'HP001');

INSERT INTO book (version, public_id, book_title, category, cover_url, description, total_copies)
SELECT 0, 'HP002', 'Harry Potter and the Chamber of Secrets', 'FICTION', 'https://covers.openlibrary.org/b/id/10110416-L.jpg', 'The second book in the Harry Potter series.', 3
WHERE NOT EXISTS (SELECT 1 FROM book WHERE public_id = 'HP002');

INSERT INTO book (version, public_id, book_title, category, cover_url, description, total_copies)
SELECT 0, '1984BK', '1984', 'FICTION', 'https://covers.openlibrary.org/b/id/7222246-L.jpg', 'A dystopian novel about totalitarianism and surveillance.', 4
WHERE NOT EXISTS (SELECT 1 FROM book WHERE public_id = '1984BK');

INSERT INTO book (version, public_id, book_title, category, cover_url, description, total_copies)
SELECT 0, 'FOUNDN', 'Foundation', 'SCIFI', 'https://covers.openlibrary.org/b/id/6540771-L.jpg', 'The first novel in Isaac Asimov''s Foundation series about the fall of a galactic empire.', 3
WHERE NOT EXISTS (SELECT 1 FROM book WHERE public_id = 'FOUNDN');

INSERT INTO book (version, public_id, book_title, category, cover_url, description, total_copies)
SELECT 0, 'ROBOTS', 'I, Robot', 'SCIFI', 'https://covers.openlibrary.org/b/id/8774017-L.jpg', 'A collection of science fiction short stories about robots and artificial intelligence.', 2
WHERE NOT EXISTS (SELECT 1 FROM book WHERE public_id = 'ROBOTS');

INSERT INTO book (version, public_id, book_title, category, cover_url, description, total_copies)
SELECT 0, 'MURDER', 'Murder on the Orient Express', 'ACTION', 'https://covers.openlibrary.org/b/id/8322053-L.jpg', 'A detective novel featuring Hercule Poirot solving a murder on a train.', 4
WHERE NOT EXISTS (SELECT 1 FROM book WHERE public_id = 'MURDER');

INSERT INTO book (version, public_id, book_title, category, cover_url, description, total_copies)
SELECT 0, 'SHERLK', 'The Adventures of Sherlock Holmes', 'ACTION', 'https://covers.openlibrary.org/b/id/8429685-L.jpg', 'A collection of twelve short stories featuring the famous detective.', 5
WHERE NOT EXISTS (SELECT 1 FROM book WHERE public_id = 'SHERLK');

INSERT INTO book (version, public_id, book_title, category, cover_url, description, total_copies)
SELECT 0, '20KLEG', 'Twenty Thousand Leagues Under the Sea', 'SCIFI', 'https://covers.openlibrary.org/b/id/2096322-L.jpg', 'A classic science fiction adventure novel about Captain Nemo and his submarine.', 3
WHERE NOT EXISTS (SELECT 1 FROM book WHERE public_id = '20KLEG');

INSERT INTO book (version, public_id, book_title, category, cover_url, description, total_copies)
SELECT 0, 'TIMEMN', 'The Time Machine', 'SCIFI', 'https://covers.openlibrary.org/b/id/10387230-L.jpg', 'A science fiction novel about time travel to the future.', 2
WHERE NOT EXISTS (SELECT 1 FROM book WHERE public_id = 'TIMEMN');

INSERT INTO book (version, public_id, book_title, category, cover_url, description, total_copies)
SELECT 0, 'SHINING', 'The Shining', 'FICTION', 'https://covers.openlibrary.org/b/id/8760256-L.jpg', 'A horror novel about a family staying in an isolated haunted hotel.', 3
WHERE NOT EXISTS (SELECT 1 FROM book WHERE public_id = 'SHINING');

-- ========== AUTHOR-BOOK RELATIONSHIPS ==========
-- Link books to authors (only if book and author exist and link doesn't exist)
INSERT INTO author_book (book_id, author_id)
SELECT b.id, a.author_id FROM book b, author a 
WHERE b.public_id = 'HP001' AND a.email = 'jk.rowling@authors.com'
AND NOT EXISTS (SELECT 1 FROM author_book ab WHERE ab.book_id = b.id AND ab.author_id = a.author_id);

INSERT INTO author_book (book_id, author_id)
SELECT b.id, a.author_id FROM book b, author a 
WHERE b.public_id = 'HP002' AND a.email = 'jk.rowling@authors.com'
AND NOT EXISTS (SELECT 1 FROM author_book ab WHERE ab.book_id = b.id AND ab.author_id = a.author_id);

INSERT INTO author_book (book_id, author_id)
SELECT b.id, a.author_id FROM book b, author a 
WHERE b.public_id = '1984BK' AND a.email = 'george.orwell@authors.com'
AND NOT EXISTS (SELECT 1 FROM author_book ab WHERE ab.book_id = b.id AND ab.author_id = a.author_id);

INSERT INTO author_book (book_id, author_id)
SELECT b.id, a.author_id FROM book b, author a 
WHERE b.public_id = 'FOUNDN' AND a.email = 'isaac.asimov@authors.com'
AND NOT EXISTS (SELECT 1 FROM author_book ab WHERE ab.book_id = b.id AND ab.author_id = a.author_id);

INSERT INTO author_book (book_id, author_id)
SELECT b.id, a.author_id FROM book b, author a 
WHERE b.public_id = 'ROBOTS' AND a.email = 'isaac.asimov@authors.com'
AND NOT EXISTS (SELECT 1 FROM author_book ab WHERE ab.book_id = b.id AND ab.author_id = a.author_id);

INSERT INTO author_book (book_id, author_id)
SELECT b.id, a.author_id FROM book b, author a 
WHERE b.public_id = 'MURDER' AND a.email = 'agatha.christie@authors.com'
AND NOT EXISTS (SELECT 1 FROM author_book ab WHERE ab.book_id = b.id AND ab.author_id = a.author_id);

INSERT INTO author_book (book_id, author_id)
SELECT b.id, a.author_id FROM book b, author a 
WHERE b.public_id = 'SHERLK' AND a.email = 'arthur.doyle@authors.com'
AND NOT EXISTS (SELECT 1 FROM author_book ab WHERE ab.book_id = b.id AND ab.author_id = a.author_id);

INSERT INTO author_book (book_id, author_id)
SELECT b.id, a.author_id FROM book b, author a 
WHERE b.public_id = '20KLEG' AND a.email = 'jules.verne@authors.com'
AND NOT EXISTS (SELECT 1 FROM author_book ab WHERE ab.book_id = b.id AND ab.author_id = a.author_id);

INSERT INTO author_book (book_id, author_id)
SELECT b.id, a.author_id FROM book b, author a 
WHERE b.public_id = 'TIMEMN' AND a.email = 'hg.wells@authors.com'
AND NOT EXISTS (SELECT 1 FROM author_book ab WHERE ab.book_id = b.id AND ab.author_id = a.author_id);

INSERT INTO author_book (book_id, author_id)
SELECT b.id, a.author_id FROM book b, author a 
WHERE b.public_id = 'SHINING' AND a.email = 'stephen.king@authors.com'
AND NOT EXISTS (SELECT 1 FROM author_book ab WHERE ab.book_id = b.id AND ab.author_id = a.author_id);

-- ========== BOOK COPIES ==========
-- Create book copies for each book (only if copy doesn't exist)
INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, 'HP001-0001', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = 'HP001'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = 'HP001-0001');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, 'HP001-0002', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = 'HP001'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = 'HP001-0002');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, 'HP001-0003', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = 'HP001'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = 'HP001-0003');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, 'HP002-0001', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = 'HP002'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = 'HP002-0001');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, 'HP002-0002', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = 'HP002'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = 'HP002-0002');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, '1984BK-0001', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = '1984BK'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = '1984BK-0001');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, '1984BK-0002', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = '1984BK'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = '1984BK-0002');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, 'FOUNDN-0001', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = 'FOUNDN'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = 'FOUNDN-0001');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, 'FOUNDN-0002', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = 'FOUNDN'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = 'FOUNDN-0002');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, 'ROBOTS-0001', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = 'ROBOTS'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = 'ROBOTS-0001');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, 'MURDER-0001', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = 'MURDER'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = 'MURDER-0001');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, 'MURDER-0002', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = 'MURDER'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = 'MURDER-0002');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, 'SHERLK-0001', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = 'SHERLK'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = 'SHERLK-0001');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, 'SHERLK-0002', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = 'SHERLK'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = 'SHERLK-0002');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, '20KLEG-0001', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = '20KLEG'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = '20KLEG-0001');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, 'TIMEMN-0001', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = 'TIMEMN'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = 'TIMEMN-0001');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, 'SHINING-0001', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = 'SHINING'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = 'SHINING-0001');

INSERT INTO book_copy (version, copy_public_id, status, book_id, current_user_id)
SELECT 0, 'SHINING-0002', 'AVAILABLE', b.id, NULL FROM book b WHERE b.public_id = 'SHINING'
AND NOT EXISTS (SELECT 1 FROM book_copy bc WHERE bc.copy_public_id = 'SHINING-0002');

-- ========== ISSUED BOOKS (Borrowing Records) ==========
-- Currently borrowed books (status = BORROWED)
-- John Doe borrowed Harry Potter 1 (copy HP001-0001) - 10 days ago, due in 4 days
INSERT INTO issued_books (user_id, book_copy_id, issue_date, due_date, return_date, status, fine_amount)
SELECT u.id, bc.id, DATEADD('DAY', -10, CURRENT_DATE), DATEADD('DAY', 4, CURRENT_DATE), NULL, 'BORROWED', 0.00
FROM users u, book_copy bc
WHERE u.email = 'john.doe@email.com' AND bc.copy_public_id = 'HP001-0001'
AND NOT EXISTS (SELECT 1 FROM issued_books ib WHERE ib.user_id = u.id AND ib.book_copy_id = bc.id AND ib.status = 'BORROWED');

-- Jane Smith borrowed Harry Potter 2 (copy HP002-0001) - 5 days ago, due in 9 days  
INSERT INTO issued_books (user_id, book_copy_id, issue_date, due_date, return_date, status, fine_amount)
SELECT u.id, bc.id, DATEADD('DAY', -5, CURRENT_DATE), DATEADD('DAY', 9, CURRENT_DATE), NULL, 'BORROWED', 0.00
FROM users u, book_copy bc
WHERE u.email = 'jane.smith@email.com' AND bc.copy_public_id = 'HP002-0001'
AND NOT EXISTS (SELECT 1 FROM issued_books ib WHERE ib.user_id = u.id AND ib.book_copy_id = bc.id AND ib.status = 'BORROWED');

-- Alice Brown borrowed Sherlock Holmes (copy SHERLK-0001) - 7 days ago, due in 7 days
INSERT INTO issued_books (user_id, book_copy_id, issue_date, due_date, return_date, status, fine_amount)
SELECT u.id, bc.id, DATEADD('DAY', -7, CURRENT_DATE), DATEADD('DAY', 7, CURRENT_DATE), NULL, 'BORROWED', 0.00
FROM users u, book_copy bc
WHERE u.email = 'alice.brown@email.com' AND bc.copy_public_id = 'SHERLK-0001'
AND NOT EXISTS (SELECT 1 FROM issued_books ib WHERE ib.user_id = u.id AND ib.book_copy_id = bc.id AND ib.status = 'BORROWED');

-- Overdue book - Bob Wilson borrowed 1984 (copy 1984BK-0001) - 20 days ago, was due 6 days ago
INSERT INTO issued_books (user_id, book_copy_id, issue_date, due_date, return_date, status, fine_amount)
SELECT u.id, bc.id, DATEADD('DAY', -20, CURRENT_DATE), DATEADD('DAY', -6, CURRENT_DATE), NULL, 'OVERDUE', 30.00
FROM users u, book_copy bc
WHERE u.email = 'bob.wilson@email.com' AND bc.copy_public_id = '1984BK-0001'
AND NOT EXISTS (SELECT 1 FROM issued_books ib WHERE ib.user_id = u.id AND ib.book_copy_id = bc.id AND ib.status IN ('BORROWED', 'OVERDUE'));

-- Returned books (historical records)
-- John Doe returned Foundation (copy FOUNDN-0001) - 30 days ago, returned on time
INSERT INTO issued_books (user_id, book_copy_id, issue_date, due_date, return_date, status, fine_amount)
SELECT u.id, bc.id, DATEADD('DAY', -30, CURRENT_DATE), DATEADD('DAY', -16, CURRENT_DATE), DATEADD('DAY', -17, CURRENT_DATE), 'RETURNED', 0.00
FROM users u, book_copy bc
WHERE u.email = 'john.doe@email.com' AND bc.copy_public_id = 'FOUNDN-0001'
AND NOT EXISTS (SELECT 1 FROM issued_books ib WHERE ib.user_id = u.id AND ib.book_copy_id = bc.id AND ib.return_date = DATEADD('DAY', -17, CURRENT_DATE));

-- Jane Smith returned Murder on Orient (copy MURDER-0001) - 25 days ago, returned on time
INSERT INTO issued_books (user_id, book_copy_id, issue_date, due_date, return_date, status, fine_amount)
SELECT u.id, bc.id, DATEADD('DAY', -25, CURRENT_DATE), DATEADD('DAY', -11, CURRENT_DATE), DATEADD('DAY', -12, CURRENT_DATE), 'RETURNED', 0.00
FROM users u, book_copy bc
WHERE u.email = 'jane.smith@email.com' AND bc.copy_public_id = 'MURDER-0001'
AND NOT EXISTS (SELECT 1 FROM issued_books ib WHERE ib.user_id = u.id AND ib.book_copy_id = bc.id AND ib.return_date = DATEADD('DAY', -12, CURRENT_DATE));

-- Alice Brown returned Time Machine (copy TIMEMN-0001) - 15 days ago, returned late with fine
INSERT INTO issued_books (user_id, book_copy_id, issue_date, due_date, return_date, status, fine_amount)
SELECT u.id, bc.id, DATEADD('DAY', -28, CURRENT_DATE), DATEADD('DAY', -20, CURRENT_DATE), DATEADD('DAY', -15, CURRENT_DATE), 'RETURNED', 25.00
FROM users u, book_copy bc
WHERE u.email = 'alice.brown@email.com' AND bc.copy_public_id = 'TIMEMN-0001'
AND NOT EXISTS (SELECT 1 FROM issued_books ib WHERE ib.user_id = u.id AND ib.book_copy_id = bc.id AND ib.return_date = DATEADD('DAY', -15, CURRENT_DATE));

-- Bob Wilson returned I, Robot (copy ROBOTS-0001) - 40 days ago
INSERT INTO issued_books (user_id, book_copy_id, issue_date, due_date, return_date, status, fine_amount)
SELECT u.id, bc.id, DATEADD('DAY', -50, CURRENT_DATE), DATEADD('DAY', -36, CURRENT_DATE), DATEADD('DAY', -40, CURRENT_DATE), 'RETURNED', 0.00
FROM users u, book_copy bc
WHERE u.email = 'bob.wilson@email.com' AND bc.copy_public_id = 'ROBOTS-0001'
AND NOT EXISTS (SELECT 1 FROM issued_books ib WHERE ib.user_id = u.id AND ib.book_copy_id = bc.id AND ib.return_date = DATEADD('DAY', -40, CURRENT_DATE));

-- Update book copy status for borrowed copies
UPDATE book_copy SET status = 'BORROWED', current_user_id = (SELECT id FROM users WHERE email = 'john.doe@email.com')
WHERE copy_public_id = 'HP001-0001' AND status = 'AVAILABLE';

UPDATE book_copy SET status = 'BORROWED', current_user_id = (SELECT id FROM users WHERE email = 'jane.smith@email.com')
WHERE copy_public_id = 'HP002-0001' AND status = 'AVAILABLE';

UPDATE book_copy SET status = 'BORROWED', current_user_id = (SELECT id FROM users WHERE email = 'alice.brown@email.com')
WHERE copy_public_id = 'SHERLK-0001' AND status = 'AVAILABLE';

UPDATE book_copy SET status = 'BORROWED', current_user_id = (SELECT id FROM users WHERE email = 'bob.wilson@email.com')
WHERE copy_public_id = '1984BK-0001' AND status = 'AVAILABLE';

-- ========== BOOK DONATIONS ==========
-- Pending donation from John Doe
INSERT INTO book_donation (user_id, book_title, author, quantity_offered, quantity_approved, status, created_at)
SELECT u.id, 'The Catcher in the Rye', 'J.D. Salinger', 2, 0, 'PENDING', CURRENT_DATE
FROM users u WHERE u.email = 'john.doe@email.com'
AND NOT EXISTS (SELECT 1 FROM book_donation WHERE book_title = 'The Catcher in the Rye' AND user_id = u.id);

-- Pending donation from Alice Brown
INSERT INTO book_donation (user_id, book_title, author, quantity_offered, quantity_approved, status, created_at)
SELECT u.id, 'Pride and Prejudice', 'Jane Austen', 1, 0, 'PENDING', DATEADD('DAY', -2, CURRENT_DATE)
FROM users u WHERE u.email = 'alice.brown@email.com'
AND NOT EXISTS (SELECT 1 FROM book_donation WHERE book_title = 'Pride and Prejudice' AND user_id = u.id);

-- Approved donation from Jane Smith
INSERT INTO book_donation (user_id, book_title, author, quantity_offered, quantity_approved, status, created_at, processed_at, admin_notes)
SELECT u.id, 'The Hobbit', 'J.R.R. Tolkien', 5, 5, 'APPROVED', DATEADD('DAY', -10, CURRENT_DATE), DATEADD('DAY', -8, CURRENT_DATE), 'Great condition, added to inventory.'
FROM users u WHERE u.email = 'jane.smith@email.com'
AND NOT EXISTS (SELECT 1 FROM book_donation WHERE book_title = 'The Hobbit' AND user_id = u.id);

-- Rejected donation from Bob Wilson
INSERT INTO book_donation (user_id, book_title, author, quantity_offered, quantity_approved, status, created_at, processed_at, admin_notes)
SELECT u.id, 'Outdated Encyclopedia Vol 1', 'Various', 1, 0, 'REJECTED', DATEADD('DAY', -15, CURRENT_DATE), DATEADD('DAY', -14, CURRENT_DATE), 'Too old, not accepting encyclopedias before 2010.'
FROM users u WHERE u.email = 'bob.wilson@email.com'
AND NOT EXISTS (SELECT 1 FROM book_donation WHERE book_title = 'Outdated Encyclopedia Vol 1' AND user_id = u.id);

-- Additional Pending Donation from Staff Member
INSERT INTO book_donation (user_id, book_title, author, quantity_offered, quantity_approved, status, created_at)
SELECT u.id, 'Clean Code', 'Robert C. Martin', 3, 0, 'PENDING', CURRENT_DATE
FROM users u WHERE u.email = 'staff@library.com'
AND NOT EXISTS (SELECT 1 FROM book_donation WHERE book_title = 'Clean Code' AND user_id = u.id);

-- Additional Pending Donation from John Doe
INSERT INTO book_donation (user_id, book_title, author, quantity_offered, quantity_approved, status, created_at)
SELECT u.id, 'Design Patterns', 'Erich Gamma et al.', 2, 0, 'PENDING', DATEADD('DAY', -1, CURRENT_DATE)
FROM users u WHERE u.email = 'john.doe@email.com'
AND NOT EXISTS (SELECT 1 FROM book_donation WHERE book_title = 'Design Patterns' AND user_id = u.id);

-- ========== COMPLAINTS ==========

-- 1. Pending Complaint (Book Damage) - John Doe
INSERT INTO complaints (version, complaint_id, user_id, subject, description, category, status, created_at, updated_at, first_staff_rejected, second_staff_rejected)
SELECT 0, 'CMP-001', u.id, 'Damaged Book Received', 'The copy of Harry Potter I borrowed has torn pages in chapter 5.', 'BOOK_DAMAGE', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, false
FROM users u WHERE u.email = 'john.doe@email.com'
AND NOT EXISTS (SELECT 1 FROM complaints WHERE complaint_id = 'CMP-001');

-- 2. Assigned Complaint (Fine Dispute) - Jane Smith assigned to Staff Member
INSERT INTO complaints (version, complaint_id, user_id, subject, description, category, status, assigned_staff_id, created_at, assigned_at, updated_at, first_staff_rejected, second_staff_rejected)
SELECT 0, 'CMP-002', u.id, 'Incorrect Fine Calculation', 'I was charged a fine even though I returned the book on time.', 'FINE_DISPUTE', 'ASSIGNED', s.id, DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, false, false
FROM users u, users s
WHERE u.email = 'jane.smith@email.com' AND s.email = 'staff@library.com'
AND NOT EXISTS (SELECT 1 FROM complaints WHERE complaint_id = 'CMP-002');

-- 3. Resolved Complaint (Facility) - Alice Brown resolved by Staff Member
INSERT INTO complaints (version, complaint_id, user_id, subject, description, category, status, assigned_staff_id, staff_response, resolution_notes, created_at, assigned_at, resolved_at, updated_at, first_staff_rejected, second_staff_rejected)
SELECT 0, 'CMP-003', u.id, 'AC not working', 'The reading room AC is not cooling properly.', 'FACILITY', 'RESOLVED', s.id, 'Maintenance team has been notified and it is fixed now.', 'Issue Resolved', DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -4, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, false, false
FROM users u, users s
WHERE u.email = 'alice.brown@email.com' AND s.email = 'staff@library.com'
AND NOT EXISTS (SELECT 1 FROM complaints WHERE complaint_id = 'CMP-003');

-- 4. Escalated Complaint (Staff Behavior) - Bob Wilson (Staff rejected -> Admin)
INSERT INTO complaints (version, complaint_id, user_id, subject, description, category, status, assigned_staff_id, staff_response, admin_id, created_at, assigned_at, updated_at, first_staff_rejected, second_staff_rejected)
SELECT 0, 'CMP-004', u.id, 'Rude behavior by librarian', 'The librarian was very rude when I asked for a book.', 'STAFF_BEHAVIOR', 'ESCALATED_TO_ADMIN', s.id, 'User was shouting, I just asked him to be quiet.', a.id, DATEADD('DAY', -10, CURRENT_TIMESTAMP), DATEADD('DAY', -9, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, true, false
FROM users u, users s, users a
WHERE u.email = 'bob.wilson@email.com' AND s.email = 'staff@library.com' AND a.email = 'admin@library.com'
AND NOT EXISTS (SELECT 1 FROM complaints WHERE complaint_id = 'CMP-004');

