-- Initialize roles (already exists in schema, but ensure they are present)
INSERT INTO roles (name, description) VALUES 
('LIBRARIAN', 'Librarian - Can manage book data'),
('MEMBER', 'General user - Can search books, borrow and return')
ON CONFLICT (name) DO NOTHING;

-- Initialize libraries (update existing ones with better data)
INSERT INTO libraries (name, address, phone) VALUES 
('Central Library', '20 Zhongshan South Road, Taipei City', '02-2361-9132'),
('Technology Library', '101 Guangfu Road Section 2, Hsinchu City', '03-571-2121'),
('Humanities Library', '1 Wuquan Road, Taichung City', '04-2226-1180')
ON CONFLICT (name) DO UPDATE SET 
    address = EXCLUDED.address,
    phone = EXCLUDED.phone;


-- User data
INSERT INTO users (name, password, email, role_id, librarian_id, is_verified) VALUES 
-- General users (MEMBER role)
('John Doe', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'john.doe@example.com', 
 (SELECT id FROM roles WHERE name = 'MEMBER'), NULL, FALSE),
('Jane Smith', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'jane.smith@example.com', 
 (SELECT id FROM roles WHERE name = 'MEMBER'), NULL, FALSE),
-- Librarian (LIBRARIAN role, verified)
('Librarian Admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'librarian@library.com', 
 (SELECT id FROM roles WHERE name = 'LIBRARIAN'), 'LIB001', TRUE)
ON CONFLICT (email) DO NOTHING;

-- Initialize books
INSERT INTO books (title, author, published_year, category, book_type) VALUES
('Java Programming Practice Guide', 'John Smith', 2023, 'Programming', '圖書'),
('Database Systems Introduction', 'Mary Johnson', 2022, 'Computer Science', '圖書'),
('Algorithms and Data Structures', 'David Wilson', 2021, 'Computer Science', '圖書'),
('Software Engineering Best Practices', 'Sarah Chen', 2024, 'Software Engineering', '書籍'),
('Artificial Intelligence and Machine Learning', 'Michael Brown', 2023, 'Artificial Intelligence', '書籍');

-- Initialize book copies
-- Each book has 1-3 copies distributed across different libraries

-- Java Programming Practice Guide - 3 copies
INSERT INTO book_copies (book_id, library_id, copy_number, status) VALUES
(1, 1, 1, 'AVAILABLE'),
(1, 2, 2, 'AVAILABLE'),
(1, 3, 3, 'AVAILABLE');

-- Database Systems Introduction - 2 copies
INSERT INTO book_copies (book_id, library_id, copy_number, status) VALUES
(2, 1, 1, 'AVAILABLE'),
(2, 2, 2, 'AVAILABLE');

-- Algorithms and Data Structures - 3 copies
INSERT INTO book_copies (book_id, library_id, copy_number, status) VALUES
(3, 1, 1, 'AVAILABLE'),
(3, 2, 2, 'AVAILABLE'),
(3, 3, 3, 'AVAILABLE');

-- Software Engineering Best Practices - 1 copy
INSERT INTO book_copies (book_id, library_id, copy_number, status) VALUES
(4, 1, 1, 'AVAILABLE');

-- Artificial Intelligence and Machine Learning - 2 copies
INSERT INTO book_copies (book_id, library_id, copy_number, status) VALUES
(5, 2, 1, 'AVAILABLE'),
(5, 3, 2, 'AVAILABLE');
