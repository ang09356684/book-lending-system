# 線上圖書借閱系統 - 開發TODO清單
## 適合沒有Spring Boot經驗的後端工程師

---

### 📋 **開發前準備**

#### **階段0: 環境準備** ⏱️ 30分鐘
- [x] **安裝必要工具**
  - [x] 安裝Java 17 (JDK)
  - [x] 安裝Maven (建置工具)
  - [x] 安裝Docker Desktop
  - [ ] 安裝IDE (推薦IntelliJ IDEA或VS Code)
  - [x] 安裝Git

- [x] **驗證環境**
  - [x] 確認Java版本: `java -version`
  - [x] 確認Maven版本: `mvn -version`
  - [x] 確認Docker版本: `docker --version`

**目的**: 確保開發環境正確設置，避免後續開發問題

---

### 🏗️ **專案初始化**

#### **階段1: 建立Spring Boot專案** ⏱️ 1小時
- [x] **建立專案結構**
  - [x] 使用Spring Initializr建立專案
  - [x] 設定專案基本資訊
  - [x] 選擇必要依賴套件
  - [x] 下載並解壓專案

- [x] **專案配置**
  - [x] 檢查pom.xml檔案
  - [x] 設定application.properties
  - [x] 建立基本目錄結構

**目的**: 建立一個可運行的Spring Boot專案基礎

#### **階段2: 資料庫配置** ⏱️ 1小時
- [x] **Docker Compose設定**
  - [x] 建立docker-compose.yml
  - [x] 配置PostgreSQL容器
  - [x] 設定資料庫連線參數
  - [x] 測試資料庫連線

- [x] **Spring Boot資料庫整合**
  - [x] 配置application.properties
  - [x] 設定JPA/Hibernate
  - [x] 測試資料庫連線

**目的**: 建立資料庫環境，確保應用程式能正確連接資料庫

---

### 📊 **資料庫層開發**

#### **階段3: 實體類別建立** ⏱️ 2小時
- [x] **建立Entity類別**
  - [x] 建立User實體
  - [x] 建立Role實體
  - [x] 建立Book實體
  - [x] 建立BookCopy實體
  - [x] 建立Library實體
  - [x] 建立BorrowRecord實體
  - [x] 建立Notification實體

- [x] **設定關聯關係**
  - [x] 設定OneToMany/ManyToOne關係
  - [x] 設定外鍵約束
  - [x] 設定級聯操作

**目的**: 建立資料庫表結構的Java物件對應

#### **階段4: Repository層** ⏱️ 1小時
- [x] **建立Repository介面**
  - [x] UserRepository
  - [x] BookRepository
  - [x] BookCopyRepository
  - [x] BorrowRecordRepository
  - [x] LibraryRepository
  - [x] RoleRepository
  - [x] NotificationRepository

- [x] **自定義查詢方法**
  - [x] 根據書名搜尋書籍
  - [x] 根據作者搜尋書籍
  - [x] 查詢可用副本
  - [x] 查詢使用者借閱記錄
  - [x] 逾期查詢功能
  - [x] 分頁查詢功能

**目的**: 建立資料存取層，提供資料庫操作方法

---

### 🔧 **業務邏輯層開發**

#### **階段5: Service層** ⏱️ 3小時
- [x] **使用者管理服務**
  - [x] UserService - 註冊功能
  - [x] UserService - 登入功能
  - [x] UserService - 館員驗證功能
  - [x] 密碼加密處理

- [x] **書籍管理服務**
  - [x] BookService - 新增書籍
  - [x] BookService - 搜尋書籍
  - [x] BookService - 更新書籍
  - [x] BookService - 刪除書籍

- [x] **借閱管理服務**
  - [x] BorrowService - 借書功能
  - [x] BorrowService - 還書功能
  - [x] BorrowService - 借閱限制檢查
  - [x] BorrowService - 逾期檢查

- [x] **圖書館管理服務**
  - [x] LibraryService - 圖書館管理
  - [x] NotificationService - 通知管理

**目的**: 實現核心業務邏輯，處理複雜的業務規則

#### **階段6: 外部API整合** ⏱️ 1小時
- [x] **館員驗證API整合**
  - [x] 建立外部API服務類別
  - [x] 實作HTTP請求
  - [x] 處理API回應
  - [x] 錯誤處理機制

**目的**: 整合外部系統，實現館員身份驗證

---

### 🌐 **API層開發**

#### **階段7: Controller層** ⏱️ 2小時
- [x] **認證Controller**
  - [x] AuthController - 註冊API
  - [x] AuthController - 館員註冊API
  - [x] AuthController - 使用者檢查API

- [x] **書籍管理Controller**
  - [x] BookController - 書籍CRUD API
  - [x] BookController - 書籍搜尋API
  - [x] BookController - 書籍可用性檢查API

- [x] **借閱管理Controller**
  - [x] BorrowController - 借書API
  - [x] BorrowController - 還書API
  - [x] BorrowController - 查詢借閱記錄API

- [x] **使用者管理Controller**
  - [x] UserController - 使用者查詢API
  - [x] UserController - 使用者狀態更新API

- [x] **圖書館管理Controller**
  - [x] LibraryController - 圖書館CRUD API

- [x] **統一API回應格式**
  - [x] ApiResponse DTO類別
  - [x] 全域異常處理器
  - [x] 請求驗證DTO類別

**目的**: 建立RESTful API端點，提供HTTP介面

#### **階段8: 權限控制** ⏱️ 1小時
- [x] **Spring Security配置**
  - [x] 設定JWT認證
  - [x] 配置角色權限
  - [x] 設定API端點權限
  - [x] 測試權限控制

**目的**: 實現API安全性，確保只有授權使用者能存取特定功能

---

### 📝 **API文件**

#### **階段9: Springdoc OpenAPI** ⏱️ 30分鐘
- [x] **API文件配置**
  - [x] 加入Springdoc OpenAPI依賴
  - [x] 配置OpenAPI設定
  - [x] 為Controller加入API註解
  - [x] 測試API文件頁面

**目的**: 產生互動式API文件，方便測試和文件化

---

### 🧪 **測試開發**

#### **階段10: 單元測試** ⏱️ 2小時
- [ ] **Service層測試**
  - [ ] UserService測試
  - [ ] BookService測試
  - [ ] BorrowService測試
  - [ ] 使用Mockito模擬依賴

- [ ] **Repository層測試**
  - [ ] 使用@DataJpaTest
  - [ ] 測試資料庫操作
  - [ ] 使用PostgreSQL資料庫

- [ ] **Controller層測試**
  - [ ] 使用@WebMvcTest
  - [ ] 測試API端點
  - [ ] 驗證HTTP狀態碼

**目的**: 確保程式碼品質，驗證功能正確性

---

### 🐳 **Docker容器化**

#### **階段11: Docker配置** ⏱️ 1小時
- [x] **Dockerfile建立**
  - [x] 多階段建置配置
  - [x] 設定Java環境
  - [x] 複製應用程式檔案
  - [x] 設定啟動指令

- [x] **Docker Compose完善**
  - [x] 配置應用程式容器
  - [x] 設定網路連接
  - [x] 配置環境變數
  - [x] 加入健康檢查

**目的**: 容器化應用程式，確保部署一致性

---

### 📚 **文件與部署**

#### **階段12: 文件與部署** ⏱️ 1小時
- [ ] **README文件**
  - [ ] 專案說明
  - [ ] 環境需求
  - [ ] 安裝步驟
  - [ ] 運行說明
  - [ ] API使用範例

- [ ] **最終測試**
  - [ ] 完整功能測試
  - [ ] Docker容器測試
  - [ ] API文件測試
  - [ ] 單元測試執行

**目的**: 完成專案文件，確保專案可正常運行

---

### ⚠️ **注意事項**

#### **開發原則**
1. **逐步開發**: 每個階段完成後都要測試
2. **版本控制**: 每完成一個功能就commit
3. **錯誤處理**: 每個API都要有適當的錯誤處理
4. **程式碼品質**: 遵循Java編碼規範

#### **測試策略**
1. **單元測試**: 每個Service方法都要有測試
2. **整合測試**: 測試API端點功能
3. **手動測試**: 使用Postman或API文件頁面測試

#### **中斷恢復**
- 每個階段都有明確的完成標準
- 可以從任何階段重新開始
- 所有配置檔案都有備份

---

### 📊 **進度追蹤**

#### **完成度檢查清單**
- [x] 環境準備 (5/5)
- [x] 專案初始化 (4/4)
- [x] 資料庫配置 (4/4)
- [ ] 實體類別建立 (0/7)
- [ ] Repository層 (0/5)
- [ ] Service層 (0/12)
- [ ] 外部API整合 (0/4)
- [ ] Controller層 (0/8)
- [x] 權限控制 (4/4)
- [ ] API文件 (0/4)
- [ ] 單元測試 (0/9)
- [x] Docker配置 (4/4)
- [ ] 文件與部署 (0/5)

**總進度**: 60/75 項目完成

---

### 🎯 **成功標準**

#### **功能完整性**
- ✅ 使用者註冊登入功能
- ✅ 館員驗證API整合
- ✅ 書籍管理功能
- ✅ 借閱還書功能
- ✅ 到期通知功能

#### **技術標準**
- ✅ 程式碼可編譯運行
- ✅ API端點正常運作
- ✅ 資料庫操作正確
- ✅ Docker容器正常啟動
- ✅ API文件頁面可訪問
- ✅ 單元測試通過

#### **品質標準**
- ✅ 程式碼結構清晰
- ✅ 遵循Spring Boot最佳實踐
- ✅ 完整的錯誤處理
- ✅ 良好的程式碼註解

---

**準備好開始了嗎？我們可以從階段0開始，我會詳細說明每個步驟！**
