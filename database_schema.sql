-- Online Library Management System - PostgreSQL Schema
-- Complete SQL file, ready to execute

-- ========================================
-- 1. Roles and Users
-- ========================================

-- Roles table (Librarian / Member)
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,  -- 'LIBRARIAN', 'MEMBER'
    description TEXT
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,            -- User's full name
    password VARCHAR(255) NOT NULL,        -- Password (needs encryption)
    email VARCHAR(100) UNIQUE NOT NULL,    -- Email (used for login)
    role_id BIGINT NOT NULL,
    librarian_id VARCHAR(50),              -- Librarian ID (librarians only)
    is_verified BOOLEAN DEFAULT FALSE,     -- Librarian accounts need external verification
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- User related indexes
CREATE INDEX IF NOT EXISTS idx_users_role_id ON users(role_id);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_librarian_id ON users(librarian_id);



-- ========================================
-- 2. Libraries and Collections
-- ========================================

-- Libraries (Main branch / Branch libraries)
CREATE TABLE IF NOT EXISTS libraries (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Book basic information (not copies)
CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(200) NOT NULL,
    published_year INT CHECK (published_year > 0),
    category VARCHAR(50) NOT NULL,
    book_type VARCHAR(20) NOT NULL DEFAULT '圖書' CHECK (book_type IN ('圖書', '書籍')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Book related indexes
CREATE INDEX IF NOT EXISTS idx_books_category ON books(category);
CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_books_author ON books(author);
CREATE INDEX IF NOT EXISTS idx_books_published_year ON books(published_year);
CREATE INDEX IF NOT EXISTS idx_books_search ON books(title, author, published_year);

-- Book copies (one book may have multiple copies, different branches)
CREATE TABLE IF NOT EXISTS book_copies (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    library_id BIGINT NOT NULL,
    copy_number INT NOT NULL,  -- Copy number (cannot duplicate in same library)
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE'
        CHECK (status IN ('AVAILABLE','BORROWED','LOST','DAMAGED')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(book_id, library_id, copy_number)
);

-- Copy related indexes
CREATE INDEX IF NOT EXISTS idx_book_copies_library_id ON book_copies(library_id);
CREATE INDEX IF NOT EXISTS idx_book_copies_status ON book_copies(status);
CREATE INDEX IF NOT EXISTS idx_book_copies_book_library ON book_copies(book_id, library_id);

-- ========================================
-- 3. Borrowing and Return Records
-- ========================================

-- Borrowing records
CREATE TABLE IF NOT EXISTS borrow_records (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_copy_id BIGINT NOT NULL,
    borrowed_at TIMESTAMP DEFAULT NOW(),
    due_at TIMESTAMP NOT NULL,        -- Due date (set to +1 month when borrowing)
    returned_at TIMESTAMP,            -- Return time
    status VARCHAR(20) NOT NULL DEFAULT 'BORROWED'
        CHECK (status IN ('BORROWED', 'RETURNED', 'OVERDUE')),
    UNIQUE(user_id, book_copy_id, status) -- Prevent duplicate borrowing of same book by same person
);

-- Borrowing record related indexes
CREATE INDEX IF NOT EXISTS idx_borrow_records_user_id ON borrow_records(user_id);
CREATE INDEX IF NOT EXISTS idx_borrow_records_due_at ON borrow_records(due_at);
CREATE INDEX IF NOT EXISTS idx_borrow_records_status ON borrow_records(status);
CREATE INDEX IF NOT EXISTS idx_borrow_records_user_status ON borrow_records(user_id, status);
CREATE INDEX IF NOT EXISTS idx_borrow_records_overdue ON borrow_records(user_id, due_at, status);

-- ========================================
-- 4. Due Notifications
-- ========================================

-- Borrowing due reminder notifications
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    borrow_record_id BIGSERIAL NOT NULL,
    message TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT NOW()
);

-- ========================================
-- 5. Index Design
-- ========================================

-- Indexes are defined after each table

-- ========================================
-- 6. Database Functions
-- ========================================

-- Functions will be implemented in Java Service layer

-- ========================================
-- 7. Initial Data
-- ========================================

-- Role data
INSERT INTO roles (name, description) VALUES 
('LIBRARIAN', 'Librarian - Can manage book data'),
('MEMBER', 'General user - Can search books, borrow and return')
ON CONFLICT (name) DO NOTHING;

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

-- Library data
INSERT INTO libraries (name, address, phone) VALUES 
('Main Library', 'Taipei City', '02-2361-9132'),
('Branch A', 'Taipei City', '02-2707-1008'),
('Branch B', 'Taipei City', '02-2720-8889')
ON CONFLICT (name) DO NOTHING;

-- ========================================
-- 8. Common Query Examples
-- ========================================

-- Query user borrowing statistics
-- SELECT 
--     u.full_name,
--     COUNT(br.id) as total_borrowed,
--     COUNT(CASE WHEN br.due_at < NOW() THEN 1 END) as overdue_count
-- FROM users u
-- LEFT JOIN borrow_records br ON u.id = br.user_id AND br.status = 'BORROWED'
-- GROUP BY u.id, u.full_name;

-- Query library collection statistics
-- SELECT 
--     l.name as library_name,
--     COUNT(bc.id) as total_copies,
--     COUNT(CASE WHEN bc.status = 'AVAILABLE' THEN 1 END) as available_copies
-- FROM libraries l
-- LEFT JOIN book_copies bc ON l.id = bc.library_id
-- GROUP BY l.id, l.name;

-- ========================================
-- Complete
-- ========================================
