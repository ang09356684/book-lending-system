# API測試檢查清單

## 🔐 1. 會員管理測試

### 1.1 使用者註冊

#### 一般用戶註冊
```bash
curl -X POST 'http://localhost:8080/api/auth/register' \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "test_user",
    "email": "test@example.com",
    "password": "123456"
  }'
```

**檢查項目：**
- [ ] 一般用戶註冊成功（返回201狀態碼）
- [ ] 重複email註冊失敗（返回400狀態碼）
- [ ] 密碼少於6位註冊失敗（返回400狀態碼）
- [ ] 無效email格式註冊失敗（返回400狀態碼）

#### 館員註冊
```bash
curl -X POST 'http://localhost:8080/api/auth/register/librarian' \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "test_librarian",
    "email": "librarian@example.com",
    "password": "123456",
    "librarianId": "LIB001"
  }'
```

**檢查項目：**
- [ ] 館員註冊成功（返回201狀態碼）
- [ ] 外部系統驗證館員身份
- [ ] 驗證失敗時註冊失敗

### 1.2 使用者登入

#### 一般用戶登入
```bash
curl -X POST 'http://localhost:8080/api/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "test@example.com",
    "password": "123456"
  }'
```

#### 館員登入
```bash
curl -X POST 'http://localhost:8080/api/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "librarian@example.com",
    "password": "123456"
  }'
```

**檢查項目：**
- [ ] 正確帳號密碼登入成功（返回200狀態碼和JWT token）
- [ ] 錯誤密碼登入失敗（返回401狀態碼）
- [ ] 不存在的email登入失敗（返回401狀態碼）

---

## 📚 2. 書籍管理測試

### 2.1 館員新增書籍

#### 取得館員JWT Token
```bash
# 先登入取得JWT token
TOKEN=$(curl -X POST 'http://localhost:8080/api/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "librarian@example.com",
    "password": "123456"
  }' | jq -r '.data.token')
```

#### 新增書籍
```bash
curl -X POST 'http://localhost:8080/api/books' \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "測試書籍",
    "author": "測試作者",
    "publishedYear": 2024,
    "category": "技術",
    "bookType": "圖書"
  }'
```

**檢查項目：**
- [ ] 館員新增書籍成功（返回201狀態碼）
- [ ] 一般用戶無法新增書籍（返回403狀態碼）
- [ ] 必填欄位驗證（返回400狀態碼）

### 2.2 搜尋書籍

#### 搜尋所有書籍
```bash
curl -X GET 'http://localhost:8080/api/books'
```

#### 依書名搜尋
```bash
curl -X GET 'http://localhost:8080/api/books/search?title=測試'
```

#### 依作者搜尋
```bash
curl -X GET 'http://localhost:8080/api/books/search?author=測試'
```

#### 依分類搜尋
```bash
curl -X GET 'http://localhost:8080/api/books/search?category=技術'
```

**檢查項目：**
- [ ] 搜尋結果包含書籍基本資訊
- [ ] 搜尋結果包含各館館藏數量
- [ ] 模糊搜尋功能正常
- [ ] 多條件搜尋功能正常

### 2.3 取得書籍詳情
```bash
curl -X GET 'http://localhost:8080/api/books/1'
```

**檢查項目：**
- [ ] 返回書籍完整資訊
- [ ] 包含館藏數量資訊
- [ ] 不存在的書籍返回404

---

## 📖 3. 借閱與還書測試

### 3.1 借書功能

#### 取得用戶JWT Token
```bash
# 一般用戶登入
USER_TOKEN=$(curl -X POST 'http://localhost:8080/api/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "test@example.com",
    "password": "123456"
  }' | jq -r '.data.token')
```

#### 借書
```bash
curl -X POST 'http://localhost:8080/api/borrows/1/borrow' \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $USER_TOKEN" \
  -d '{
    "userId": 1
  }'
```

**檢查項目：**
- [ ] 借書成功（返回200狀態碼）
- [ ] 超過借閱數量限制時失敗（圖書5本，書籍10本）
- [ ] 有逾期書籍時借書失敗
- [ ] 書籍不可用時借書失敗

### 3.2 還書功能
```bash
curl -X POST 'http://localhost:8080/api/borrows/1/return' \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $USER_TOKEN"
```

**檢查項目：**
- [ ] 還書成功（返回200狀態碼）
- [ ] 不存在的借閱記錄返回404
- [ ] 已還書的記錄無法重複還書

### 3.3 查詢借閱記錄

#### 查詢用戶借閱記錄
```bash
curl -X GET 'http://localhost:8080/api/borrows/user/1' \
  -H "Authorization: Bearer $USER_TOKEN"
```

#### 查詢活躍借閱
```bash
curl -X GET 'http://localhost:8080/api/borrows/user/1/active' \
  -H "Authorization: Bearer $USER_TOKEN"
```

#### 查詢逾期記錄
```bash
curl -X GET 'http://localhost:8080/api/borrows/user/1/overdue' \
  -H "Authorization: Bearer $USER_TOKEN"
```

**檢查項目：**
- [ ] 返回用戶的借閱記錄
- [ ] 活躍借閱記錄正確
- [ ] 逾期記錄正確

### 3.4 借閱限制檢查

#### 檢查是否有逾期書籍
```bash
curl -X GET 'http://localhost:8080/api/borrows/user/1/has-overdue' \
  -H "Authorization: Bearer $USER_TOKEN"
```

#### 查詢活躍借閱數量
```bash
curl -X GET 'http://localhost:8080/api/borrows/user/1/active-count' \
  -H "Authorization: Bearer $USER_TOKEN"
```

**檢查項目：**
- [ ] 逾期檢查功能正常
- [ ] 借閱數量統計正確

---

## 🏛️ 4. 圖書館管理測試

### 4.1 查詢圖書館資訊

#### 查詢所有圖書館
```bash
curl -X GET 'http://localhost:8080/api/libraries'
```

#### 查詢特定圖書館
```bash
curl -X GET 'http://localhost:8080/api/libraries/1'
```

**檢查項目：**
- [ ] 返回圖書館基本資訊
- [ ] 包含地址和電話資訊

---

## 👤 5. 用戶管理測試

### 5.1 查詢用戶資訊

#### 查詢用戶詳情
```bash
curl -X GET 'http://localhost:8080/api/users/1' \
  -H "Authorization: Bearer $USER_TOKEN"
```

#### 依email查詢用戶
```bash
curl -X GET 'http://localhost:8080/api/users/email/test@example.com' \
  -H "Authorization: Bearer $USER_TOKEN"
```

**檢查項目：**
- [ ] 返回用戶基本資訊（不包含密碼）
- [ ] 包含用戶角色資訊

---

## 🔍 6. 權限控制測試

### 6.1 館員權限測試
```bash
# 使用館員token測試書籍管理
curl -X POST 'http://localhost:8080/api/books' \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "權限測試書籍",
    "author": "測試作者",
    "publishedYear": 2024,
    "category": "測試",
    "bookType": "圖書"
  }'
```

### 6.2 一般用戶權限測試
```bash
# 使用一般用戶token測試書籍管理（應該失敗）
curl -X POST 'http://localhost:8080/api/books' \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $USER_TOKEN" \
  -d '{
    "title": "權限測試書籍",
    "author": "測試作者",
    "publishedYear": 2024,
    "category": "測試",
    "bookType": "圖書"
  }'
```

**檢查項目：**
- [ ] 館員可以管理書籍
- [ ] 一般用戶無法管理書籍（返回403）
- [ ] 未認證用戶無法訪問受保護的API（返回401）

---

## 📋 7. 測試腳本範例

### 7.1 完整測試流程
```bash
#!/bin/bash

echo "=== 開始API測試 ==="

# 1. 註冊用戶
echo "=== 1. 註冊用戶 ==="
curl -X POST 'http://localhost:8080/api/auth/register' \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "test_user",
    "email": "test@example.com",
    "password": "123456"
  }'

echo -e "\n"

# 2. 登入取得token
echo "=== 2. 登入取得token ==="
TOKEN=$(curl -X POST 'http://localhost:8080/api/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "test@example.com",
    "password": "123456"
  }' | jq -r '.data.token')

echo "Token: $TOKEN"
echo -e "\n"

# 3. 搜尋書籍
echo "=== 3. 搜尋書籍 ==="
curl -X GET 'http://localhost:8080/api/books' \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n"

# 4. 測試借書（如果有書籍的話）
echo "=== 4. 測試借書 ==="
curl -X POST 'http://localhost:8080/api/borrows/1/borrow' \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "userId": 1
  }'

echo -e "\n"
echo "=== API測試完成 ==="
```

---

## ✅ 8. 檢查清單總結

### 8.1 基本功能
- [ ] 用戶註冊功能正常
- [ ] 用戶登入功能正常
- [ ] JWT認證機制正常
- [ ] 權限控制正常

### 8.2 書籍管理
- [ ] 館員可新增書籍
- [ ] 書籍搜尋功能正常
- [ ] 書籍詳情查詢正常
- [ ] 館藏數量顯示正確

### 8.3 借閱管理
- [ ] 借書功能正常
- [ ] 還書功能正常
- [ ] 借閱限制檢查正常
- [ ] 借閱記錄查詢正常

### 8.4 系統整合
- [ ] 所有API端點正常響應
- [ ] 錯誤處理正確
- [ ] 資料一致性正常
- [ ] 系統穩定性良好

---

## 📝 使用說明

### 準備環境
1. 確保應用程式已啟動在 `http://localhost:8080`
2. 確保資料庫已初始化（執行 `make db-clear`）
3. 準備好測試用的curl命令或Postman

### 測試順序
1. **會員管理**：註冊 → 登入 → 權限測試
2. **書籍管理**：新增書籍 → 搜尋書籍 → 查詢詳情
3. **借閱管理**：借書 → 查詢記錄 → 還書
4. **系統整合**：完整流程測試

### 注意事項
- 每個測試前先保存JWT token
- 檢查HTTP狀態碼和回應內容
- 記錄測試結果和發現的問題
- 測試邊界情況和錯誤處理

### 預期結果
- 所有API端點正常響應
- 權限控制正確運作
- 資料一致性良好
- 錯誤處理適當

---

## 🔧 故障排除

### 常見問題
1. **連接失敗**：檢查應用程式是否啟動
2. **認證失敗**：檢查JWT token是否有效
3. **權限錯誤**：確認用戶角色和權限
4. **資料錯誤**：檢查資料庫初始化狀態

### 調試命令
```bash
# 檢查應用程式狀態
curl -X GET 'http://localhost:8080/actuator/health'

# 檢查資料庫連接
make logs-db

# 重新初始化資料庫
make db-clear
```
