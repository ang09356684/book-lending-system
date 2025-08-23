# 📚 線上圖書借閱系統 - 學習目錄
## Spring Boot 完整學習指南

---

### 🎯 **學習目標**
本學習指南將帶領您從零開始，完整學習Spring Boot開發，並實作一個完整的線上圖書借閱系統。

### 👨‍💻 **適合對象**
- 有後端開發經驗但沒有Java/Spring Boot經驗的工程師
- 想要學習Spring Boot最佳實踐的開發者
- 準備面試需要Spring Boot專案的求職者

---

## 📋 **學習文件清單**

### **00. 環境問題解決**
- **檔案：** `00_環境問題解決.md`
- **內容：** Docker環境設置、常見問題解決
- **重點：** 解決開發環境問題，確保專案能正常運行

### **01. 專案初始化與環境準備**
- **檔案：** `01_專案初始化與環境準備.md`
- **內容：** Maven配置、Spring Boot基礎、Docker環境
- **重點：** 建立專案基礎架構，理解Spring Boot自動配置

### **02. 資料庫層開發（即將建立）**
- **檔案：** `02_資料庫層開發.md`
- **內容：** Entity類別、Repository介面、JPA配置
- **重點：** 理解資料庫操作，掌握JPA/Hibernate

### **03. 業務邏輯層開發（即將建立）**
- **檔案：** `03_業務邏輯層開發.md`
- **內容：** Service類別、業務規則、外部API整合
- **重點：** 實作業務邏輯，處理複雜業務規則

### **04. API層開發（即將建立）**
- **檔案：** `04_API層開發.md`
- **內容：** Controller類別、RESTful API、DTO設計
- **重點：** 建立API端點，設計RESTful介面

### **05. 安全與認證（即將建立）**
- **檔案：** `05_安全與認證.md`
- **內容：** Spring Security、JWT認證、權限控制
- **重點：** 實作安全機制，保護API端點

### **06. 測試開發（即將建立）**
- **檔案：** `06_測試開發.md`
- **內容：** 單元測試、整合測試、測試最佳實踐
- **重點：** 確保程式碼品質，提高系統穩定性

### **07. 權限控制建立**
- **檔案：** `07_階段7_權限控制建立.md`
- **內容：** Spring Security、JWT認證、權限控制
- **重點：** 實作安全機制，保護API端點

### **08. Java套件學習**
- **檔案：** `java-packages/` 目錄
- **內容：** Lombok、Spring Data JPA、Spring Security、JWT、Optional等套件詳解
- **重點：** 深入學習各套件的使用方法和最佳實踐

### **09. Swagger API文檔建立**
- **檔案：** `11_階段11_Swagger_API文檔建立.md`
- **內容：** Springdoc OpenAPI、API文檔註解、Swagger UI配置
- **重點：** 建立完整的API文檔系統

### **10. Spring Scheduled Cronjob實作**
- **檔案：** `12_階段12_Spring_Scheduled_Cronjob實作.md`
- **內容：** @Scheduled註解、Cron表達式、定時任務實作
- **重點：** 建立自動化的業務流程

---

## 🎓 **學習路徑建議**

### **初學者路徑**
1. **環境問題解決** → 確保開發環境正常
2. **專案初始化** → 理解Spring Boot基礎
3. **Entity層建立** → 學習JPA實體設計
4. **Repository層建立** → 學習資料庫操作
5. **Service層建立** → 實作業務邏輯
6. **外部API整合** → 學習外部服務整合
7. **Controller層建立** → 建立對外API介面
8. **權限控制建立** → 保護系統安全
9. **PRD業務規則實作** → 實作完整業務流程
10. **Swagger API文檔建立** → 建立API文檔
11. **Spring Scheduled Cronjob實作** → 建立自動化流程

### **有經驗開發者路徑**
- 可以跳過環境問題解決
- 重點關注Spring Boot特有概念
- 深入學習最佳實踐

---

## 🛠️ **技術棧學習**

### **核心技術**
- **Java 17**：現代Java語言特性
- **Spring Boot 3.x**：企業級框架
- **Spring Data JPA**：資料庫操作
- **Spring Security**：安全認證
- **Spring Scheduled**：定時任務框架
- **PostgreSQL**：關聯式資料庫

### **開發工具**
- **Maven**：專案建置工具
- **Docker**：容器化技術
- **JUnit 5**：測試框架
- **Swagger**：API文件

### **設計模式**
- **分層架構**：Controller → Service → Repository
- **依賴注入**：Spring IoC容器
- **RESTful API**：Web服務設計
- **JWT認證**：無狀態認證
- **定時任務**：自動化業務流程

---

## 📊 **學習進度追蹤**

### **階段0：環境準備**
- [x] 環境問題解決
- [x] 專案初始化與環境準備
- [ ] Docker環境測試

### **階段1：資料庫層開發**
- [ ] Entity類別建立
- [ ] Repository介面建立
- [ ] 資料庫連線測試

### **階段2：業務邏輯層開發**
- [ ] Service類別建立
- [ ] 業務規則實作
- [ ] 外部API整合

### **階段3：API層開發**
- [ ] Controller類別建立
- [ ] RESTful API設計
- [ ] DTO類別建立

### **階段4：安全與認證**
- [ ] Spring Security配置
- [ ] JWT認證實作
- [ ] 權限控制

### **階段5：測試與文件**
- [ ] 單元測試
- [ ] 整合測試
- [ ] API文件生成

---

## 🎯 **學習成果**

### **完成後您將能夠：**
- ✅ 理解Spring Boot架構和最佳實踐
- ✅ 實作完整的RESTful API
- ✅ 設計和操作資料庫
- ✅ 實作安全認證機制
- ✅ 撰寫高品質的測試
- ✅ 使用Docker部署應用程式
- ✅ 生成API文件

### **專案特色：**
- 🏗️ **完整的分層架構**
- 🔐 **安全的認證機制**
- 📊 **完整的資料庫設計**
- 🧪 **全面的測試覆蓋**
- 📚 **詳細的API文件**
- 🐳 **容器化部署**

---

## 💡 **學習建議**

### **1. 循序漸進**
- 按照文件順序學習
- 每個階段都要實際動手實作
- 遇到問題先查看對應的學習文件

### **2. 動手實作**
- 不要只看文件，要實際寫程式碼
- 嘗試修改和擴展功能
- 理解每個步驟的原因

### **3. 記錄問題**
- 記錄學習過程中遇到的問題
- 整理解決方案
- 建立自己的知識庫

### **4. 深入理解**
- 不只是會用，要理解原理
- 了解Spring Boot的自動配置
- 掌握設計模式和最佳實踐

---

## 🔗 **相關資源**

### **官方文件**
- [Spring Boot官方文件](https://spring.io/projects/spring-boot)
- [Spring Data JPA文件](https://spring.io/projects/spring-data-jpa)
- [Spring Security文件](https://spring.io/projects/spring-security)

### **學習資源**
- [Spring Boot Guides](https://spring.io/guides)
- [Spring Boot Tutorial](https://www.baeldung.com/spring-boot)
- [Docker官方文件](https://docs.docker.com/)

### **工具資源**
- [Maven官方文件](https://maven.apache.org/guides/)
- [PostgreSQL文件](https://www.postgresql.org/docs/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)

---

## 📞 **學習支援**

### **遇到問題時：**
1. 先查看對應的學習文件
2. 檢查錯誤訊息和日誌
3. 參考官方文件
4. 在社群中尋求幫助

### **學習社群：**
- Spring Boot官方論壇
- Stack Overflow
- GitHub Issues

---

**準備好開始您的Spring Boot學習之旅了嗎？讓我們從第一個文件開始！**
