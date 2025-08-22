# 跨平台開發指南
## Mac → Windows 無縫開發

---

## 🎯 **開發環境策略**

### **推薦方案：Docker容器化開發**
- ✅ 完全跨平台
- ✅ 環境一致性
- ✅ 團隊協作友好
- ✅ 部署一致性

---

## 🚀 **快速開始**

### **在任何平台（Mac/Windows/Linux）上**

#### **1. 安裝必要工具**
```bash
# 只需要安裝Docker Desktop
# 不需要安裝Java、Maven等開發工具
```

#### **2. 啟動開發環境**
```bash
# 克隆專案
git clone <your-repo-url>
cd library-system

# 啟動開發環境
docker-compose -f docker-compose.dev.yml up --build
```

#### **3. 訪問服務**
- **應用程式**: http://localhost:8080
- **API文件**: http://localhost:8080/swagger-ui.html
- **資料庫管理**: http://localhost:5050 (pgAdmin)
- **資料庫**: localhost:5432

---

## 🛠️ **開發工作流程**

### **程式碼編輯**
```bash
# 使用任何IDE編輯本機檔案
# 檔案會自動同步到容器中
code .  # VS Code
# 或
idea .  # IntelliJ IDEA
```

### **重新啟動應用**
```bash
# 修改程式碼後，重新啟動容器
docker-compose -f docker-compose.dev.yml restart app-dev

# 或重新建置
docker-compose -f docker-compose.dev.yml up --build app-dev
```

### **查看日誌**
```bash
# 查看應用程式日誌
docker-compose -f docker-compose.dev.yml logs -f app-dev

# 查看資料庫日誌
docker-compose -f docker-compose.dev.yml logs -f postgres
```

---

## 🔧 **除錯設定**

### **VS Code 除錯配置**
```json
// .vscode/launch.json
{
  "version": "0.2.0",
  "configurations": [
    {
      "name": "Debug Spring Boot",
      "type": "java",
      "request": "attach",
      "hostName": "localhost",
      "port": 5005
    }
  ]
}
```

### **IntelliJ IDEA 除錯配置**
1. Run → Edit Configurations
2. 新增 Remote JVM Debug
3. Host: localhost, Port: 5005

---

## 📁 **檔案結構**
```
library-system/
├── src/                    # 原始碼（本機編輯）
├── database_schema.sql     # 資料庫腳本
├── Dockerfile.dev         # 開發環境Dockerfile
├── docker-compose.dev.yml # 開發環境配置
├── pom.xml               # Maven配置
└── README.md             # 專案說明
```

---

## 🔄 **平台切換**

### **從Mac切換到Windows**
1. **提交程式碼**
   ```bash
   git add .
   git commit -m "Save progress on Mac"
   git push
   ```

2. **在Windows上**
   ```bash
   git clone <your-repo-url>
   cd library-system
   docker-compose -f docker-compose.dev.yml up --build
   ```

3. **繼續開發**
   - 所有環境設定都包含在Docker中
   - 不需要重新安裝任何工具

### **從Windows切換到Mac**
- 步驟完全相同
- 環境完全一致

---

## 🧪 **測試與驗證**

### **運行測試**
```bash
# 在容器中運行測試
docker-compose -f docker-compose.dev.yml exec app-dev mvn test

# 或進入容器
docker-compose -f docker-compose.dev.yml exec app-dev bash
mvn test
```

### **建置專案**
```bash
# 在容器中建置
docker-compose -f docker-compose.dev.yml exec app-dev mvn clean package
```

---

## 🐛 **常見問題**

### **端口衝突**
```bash
# 檢查端口使用情況
docker ps
lsof -i :8080

# 修改docker-compose.dev.yml中的端口映射
ports:
  - "8081:8080"  # 改用8081端口
```

### **檔案權限問題**
```bash
# 在Mac/Linux上可能需要調整權限
chmod -R 755 .
```

### **Docker資源不足**
- 增加Docker Desktop的記憶體分配
- 清理未使用的Docker資源

---

## 📊 **優勢對比**

| 方案 | 跨平台 | 環境一致性 | 團隊協作 | 學習成本 |
|------|--------|------------|----------|----------|
| **本機安裝** | ❌ | ❌ | ❌ | 低 |
| **Docker開發** | ✅ | ✅ | ✅ | 中 |
| **Dev Container** | ✅ | ✅ | ✅ | 高 |
| **Codespaces** | ✅ | ✅ | ✅ | 低 |

---

## 🎯 **建議**

### **個人開發**
- 使用Docker容器化開發
- 簡單、快速、一致

### **團隊開發**
- 使用Dev Container或Codespaces
- 更好的團隊協作體驗

### **面試展示**
- 使用Docker容器化開發
- 展示容器化技術能力

---

**這樣您就可以在任何平台上無縫開發了！**
