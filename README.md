# 線上圖書借閱系統
## Java + Spring Boot 專案

---

### 📁 **專案結構**

```
library-system/
├── docs/                          # 📚 文件資料夾
│   ├── PRD_線上圖書借閱系統_輕量版.md    # 產品需求文件
│   ├── TODO_開發清單.md              # 開發進度清單
│   ├── Database_Design_Document.md  # 資料庫設計文件
│   └── README_跨平台開發.md          # 跨平台開發指南
├── src/                           # 🔧 原始碼（開發時建立）
├── database_schema.sql            # 🗄️ 資料庫Schema
├── Dockerfile.dev                 # 🐳 開發環境Dockerfile
├── docker-compose.dev.yml         # 🐳 開發環境Docker Compose
├── pom.xml                        # 📦 Maven配置（開發時建立）
└── README.md                      # 📖 本文件
```

---

### 🚀 **快速開始**

#### **環境需求**
- Docker Desktop
- Git

#### **啟動開發環境**
```bash
# 1. 克隆專案
git clone <your-repo-url>
cd library-system

# 2. 啟動開發環境
docker-compose -f docker-compose.dev.yml up --build

# 3. 訪問服務
# 應用程式: http://localhost:8080
# API文件: http://localhost:8080/swagger-ui.html
# 資料庫管理: http://localhost:5050
```

---

### 📚 **文件說明**

#### **docs/ 資料夾**
- **PRD文件**: 產品需求規格，定義功能需求
- **TODO清單**: 開發進度追蹤，75個具體項目
- **資料庫設計**: 完整的資料庫Schema和設計說明
- **跨平台指南**: Mac/Windows無縫開發指南

#### **開發檔案**
- **database_schema.sql**: 可直接執行的PostgreSQL腳本
- **Dockerfile.dev**: 開發環境容器配置
- **docker-compose.dev.yml**: 一鍵啟動完整開發環境

---

### 🎯 **開發流程**

#### **1. 需求確認**
```bash
# 查看PRD文件
open docs/PRD_線上圖書借閱系統_輕量版.md
```

#### **2. 進度追蹤**
```bash
# 查看開發清單
open docs/TODO_開發清單.md
```

#### **3. 資料庫設計**
```bash
# 查看資料庫設計
open docs/Database_Design_Document.md
```

#### **4. 開始開發**
```bash
# 啟動開發環境
docker-compose -f docker-compose.dev.yml up --build
```

---

### 🛠️ **技術棧**

- **後端**: Spring Boot 3.x + Java 17
- **資料庫**: PostgreSQL 17
- **容器化**: Docker + Docker Compose
- **API文件**: Springdoc OpenAPI 3
- **測試**: JUnit 5 + Mockito

---

### 📊 **功能特色**

- ✅ 會員管理（館員/一般用戶）
- ✅ 書籍管理（多館館藏）
- ✅ 借閱還書系統
- ✅ 到期通知功能
- ✅ 權限控制
- ✅ API文件自動生成
- ✅ 完整測試覆蓋
- ✅ 跨平台開發支援

---

### 🔄 **跨平台開發**

支援在Mac、Windows、Linux之間無縫切換開發：
- 只需要Docker Desktop
- 環境完全一致
- 一鍵啟動開發環境

詳細說明請參考：`docs/README_跨平台開發.md`

---

### 📝 **開發進度**

- [ ] 環境準備
- [ ] 專案初始化
- [ ] 資料庫配置
- [ ] 實體類別建立
- [ ] Repository層
- [ ] Service層
- [ ] Controller層
- [ ] 權限控制
- [ ] API文件
- [ ] 單元測試
- [ ] Docker配置
- [ ] 文件與部署

詳細進度請參考：`docs/TODO_開發清單.md`

---

### 🎯 **面試重點**

本專案展示：
- Spring Boot開發能力
- 資料庫設計能力
- Docker容器化技術
- API設計能力
- 測試驅動開發
- 跨平台開發能力

---

**準備好開始開發了嗎？請參考 `docs/TODO_開發清單.md` 開始您的開發之旅！**
