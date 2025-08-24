# 資料庫設計文件
## 線上圖書借閱系統 - PostgreSQL Schema

---

### 文件資訊
- **專案名稱**: 線上圖書借閱系統
- **資料庫**: PostgreSQL
- **版本**: 1.0.0
- **建立日期**: 2024年
- **文件狀態**: 初版

---

## 1. 資料庫概覽

### 1.1 設計目標
- 支援會員管理（館員/一般用戶）
- 支援多館館藏管理
- 支援借閱還書流程
- 支援到期通知系統
- 確保資料完整性和一致性

### 1.2 核心實體
- **使用者 (Users)**: 館員和一般用戶
- **書籍 (Books)**: 書籍基本資訊
- **副本 (Book Copies)**: 各館館藏副本
- **借閱記錄 (Borrow Records)**: 借閱還書記錄
- **通知 (Notifications)**: 到期提醒通知

---

## 2. 資料表結構

### 2.1 角色與使用者

#### 2.1.1 角色表 (roles)
```sql
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,  -- 'LIBRARIAN', 'MEMBER'
    description TEXT
);
```

**欄位說明**:
- `id`: 角色唯一識別碼
- `name`: 角色名稱（LIBRARIAN/MEMBER）
- `description`: 角色描述

#### 2.1.2 使用者表 (users)
```sql
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
```

**欄位說明**:
- `id`: 使用者唯一識別碼
- `name`: 使用者姓名
- `password`: 加密密碼
- `email`: 電子郵件（唯一）
- `role_id`: 角色ID（注意：沒有外鍵約束）
- `librarian_id`: 館員識別碼（館員專用）
- `is_verified`: 驗證狀態（館員專用）
- `created_at`: 建立時間
- `updated_at`: 更新時間

### 2.2 圖書館與館藏

#### 2.2.1 圖書館表 (libraries)
```sql
CREATE TABLE IF NOT EXISTS libraries (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

**欄位說明**:
- `id`: 圖書館唯一識別碼
- `name`: 圖書館名稱（唯一）
- `address`: 地址
- `phone`: 電話號碼
- `created_at`: 建立時間
- `updated_at`: 更新時間

#### 2.2.2 書籍表 (books)
```sql
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
```

**欄位說明**:
- `id`: 書籍唯一識別碼
- `title`: 書名
- `author`: 作者
- `published_year`: 出版年份
- `category`: 書籍類別
- `book_type`: 書籍類型（圖書/書籍）
- `created_at`: 建立時間
- `updated_at`: 更新時間

#### 2.2.3 書籍副本表 (book_copies)
```sql
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
```

**欄位說明**:
- `id`: 副本唯一識別碼
- `book_id`: 書籍ID（注意：沒有外鍵約束）
- `library_id`: 圖書館ID（注意：沒有外鍵約束）
- `copy_number`: 副本編號
- `status`: 副本狀態
- `created_at`: 建立時間
- `updated_at`: 更新時間

### 2.3 借閱與還書

#### 2.3.1 借閱記錄表 (borrow_records)
```sql
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
```

**欄位說明**:
- `id`: 借閱記錄唯一識別碼
- `user_id`: 使用者ID（注意：沒有外鍵約束）
- `book_copy_id`: 書籍副本ID（注意：沒有外鍵約束）
- `borrowed_at`: 借閱時間
- `due_at`: 到期時間
- `returned_at`: 歸還時間
- `status`: 借閱狀態

### 2.4 通知系統

#### 2.4.1 通知表 (notifications)
```sql
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    borrow_record_id BIGSERIAL NOT NULL,
    message TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT NOW()
);
```

**欄位說明**:
- `id`: 通知唯一識別碼
- `user_id`: 使用者ID（注意：沒有外鍵約束）
- `borrow_record_id`: 借閱記錄ID（注意：沒有外鍵約束）
- `message`: 通知訊息
- `sent_at`: 發送時間

---

## 3. 實體關係圖 (ERD)

```
roles (1) ──< users (1) ──< borrow_records (1) >── book_copies (1) >── books
                      |                                     |
                      |                                     |
                      |                                libraries
                      |
                      |──< notifications
```

**關係說明**:
- `roles` → `users`: 一對多（一個角色可有多個使用者）
- `users` → `borrow_records`: 一對多（一個使用者可有多個借閱記錄）
- `books` → `book_copies`: 一對多（一本書可有多個副本）
- `libraries` → `book_copies`: 一對多（一個圖書館可有多個副本）
- `book_copies` → `borrow_records`: 一對多（一個副本可有多個借閱記錄）
- `borrow_records` → `notifications`: 一對多（一個借閱記錄可有多個通知）

**注意**: 所有關係都沒有外鍵約束，僅為邏輯關係

---

## 4. 索引設計

### 4.1 效能索引
```sql
-- 使用者相關索引
CREATE INDEX IF NOT EXISTS idx_users_role_id ON users(role_id);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_librarian_id ON users(librarian_id);

-- 書籍相關索引
CREATE INDEX IF NOT EXISTS idx_books_category ON books(category);
CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_books_author ON books(author);
CREATE INDEX IF NOT EXISTS idx_books_published_year ON books(published_year);
CREATE INDEX IF NOT EXISTS idx_books_search ON books(title, author, published_year);

-- 副本相關索引
CREATE INDEX IF NOT EXISTS idx_book_copies_library_id ON book_copies(library_id);
CREATE INDEX IF NOT EXISTS idx_book_copies_status ON book_copies(status);
CREATE INDEX IF NOT EXISTS idx_book_copies_book_library ON book_copies(book_id, library_id);

-- 借閱記錄相關索引
CREATE INDEX IF NOT EXISTS idx_borrow_records_user_id ON borrow_records(user_id);
CREATE INDEX IF NOT EXISTS idx_borrow_records_due_at ON borrow_records(due_at);
CREATE INDEX IF NOT EXISTS idx_borrow_records_status ON borrow_records(status);
CREATE INDEX IF NOT EXISTS idx_borrow_records_user_status ON borrow_records(user_id, status);
CREATE INDEX IF NOT EXISTS idx_borrow_records_overdue ON borrow_records(user_id, due_at, status);

-- 通知相關索引
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
```

---

## 5. 資料庫函數

### 5.1 借閱限制檢查函數
```sql
CREATE OR REPLACE FUNCTION check_borrow_limit(
    p_user_id BIGINT,
    p_category VARCHAR(50)
) RETURNS BOOLEAN AS $$
DECLARE
    current_count INT;
    max_limit INT;
BEGIN
    -- 根據書籍類型設定借閱限制
    IF p_category = '圖書' THEN
        max_limit := 5;
    ELSIF p_category = '書籍' THEN
        max_limit := 10;
    ELSE
        RETURN FALSE;
    END IF;
    
    -- 計算當前借閱數量
    SELECT COUNT(*)
    INTO current_count
    FROM borrow_records br
    JOIN book_copies bc ON br.book_copy_id = bc.id
    JOIN books b ON bc.book_id = b.id
    WHERE br.user_id = p_user_id 
      AND br.status = 'BORROWED'
      AND b.category = p_category;
    
    RETURN current_count < max_limit;
END;
$$ LANGUAGE plpgsql;
```

### 5.2 逾期檢查函數
```sql
CREATE OR REPLACE FUNCTION has_overdue_books(
    p_user_id BIGINT
) RETURNS BOOLEAN AS $$
DECLARE
    overdue_count INT;
BEGIN
    SELECT COUNT(*)
    INTO overdue_count
    FROM borrow_records
    WHERE user_id = p_user_id 
      AND status = 'BORROWED'
      AND due_at < NOW();
    
    RETURN overdue_count > 0;
END;
$$ LANGUAGE plpgsql;
```

### 5.3 可用副本查詢函數
```sql
CREATE OR REPLACE FUNCTION get_available_copies(
    p_book_id BIGINT,
    p_library_id INT DEFAULT NULL
) RETURNS TABLE (
    copy_id BIGINT,
    library_name VARCHAR(100),
    copy_number INT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        bc.id,
        l.name,
        bc.copy_number
    FROM book_copies bc
    JOIN libraries l ON bc.library_id = l.id
    WHERE bc.book_id = p_book_id
      AND bc.status = 'AVAILABLE'
      AND (p_library_id IS NULL OR bc.library_id = p_library_id)
    ORDER BY l.name, bc.copy_number;
END;
$$ LANGUAGE plpgsql;
```

---

## 6. 初始化資料

### 6.1 角色資料
```sql
INSERT INTO roles (name, description) VALUES 
('LIBRARIAN', '館員 - 可管理書籍資料'),
('MEMBER', '一般用戶 - 可查詢書籍、借閱還書');
```

### 6.2 圖書館資料
```sql
INSERT INTO libraries (name, address, phone) VALUES 
('總館', '台北市中正區中山南路20號', '02-2361-9132'),
('分館A', '台北市大安區復興南路一段390號', '02-2707-1008'),
('分館B', '台北市信義區信義路五段7號', '02-2720-8889');
```

---

## 7. 資料完整性約束

### 7.1 檢查約束
- 書籍類型必須是 '圖書' 或 '書籍'
- 副本狀態必須是有效狀態
- 借閱狀態必須是有效狀態
- 出版年份必須大於0

### 7.2 外鍵約束
- **注意**: 本專案為了效能考量，沒有使用外鍵約束
- 資料一致性由應用程式層級控制
- 所有關係僅為邏輯關係

### 7.3 唯一約束
- 角色名稱唯一 (`roles.name`)
- 使用者電子郵件唯一 (`users.email`)
- 圖書館名稱唯一 (`libraries.name`)
- 同館同書的副本編號唯一 (`book_copies` 的 `UNIQUE(book_id, library_id, copy_number)`)
- 避免重複借閱同一副本 (`borrow_records` 的 `UNIQUE(user_id, book_copy_id, status)`)

---

## 8. 效能考量

### 8.1 查詢優化
- 針對常用查詢建立適當索引
- 使用複合索引提升搜尋效能
- 定期分析查詢執行計畫

### 8.2 資料維護
- 定期清理過期通知記錄
- 監控資料表大小和成長
- 考慮資料分區策略（如需要）

---

## 9. 安全性考量

### 9.1 資料保護
- 密碼欄位使用加密儲存
- 敏感資料存取控制
- 資料庫連線加密

### 9.2 存取控制
- 應用程式層級權限控制
- 資料庫使用者權限最小化
- 定期審計資料存取記錄

---

## 附錄

### A. 常用查詢範例
```sql
-- 查詢使用者借閱統計
SELECT 
    u.full_name,
    COUNT(br.id) as total_borrowed,
    COUNT(CASE WHEN br.due_at < NOW() THEN 1 END) as overdue_count
FROM users u
LEFT JOIN borrow_records br ON u.id = br.user_id AND br.status = 'BORROWED'
GROUP BY u.id, u.full_name;

-- 查詢各館館藏統計
SELECT 
    l.name as library_name,
    COUNT(bc.id) as total_copies,
    COUNT(CASE WHEN bc.status = 'AVAILABLE' THEN 1 END) as available_copies
FROM libraries l
LEFT JOIN book_copies bc ON l.id = bc.library_id
GROUP BY l.id, l.name;
```

### B. 版本歷史
- v1.0.0: 初始版本，包含基本功能支援

### C. 聯絡資訊
- 資料庫設計師: [待填寫]
- 最後更新: 2024年
