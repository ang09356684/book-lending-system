# 學習目錄

## 學習進度追蹤

### 已完成階段
- [x] 階段1: 基礎架構建立
- [x] 階段2: 實體設計與資料庫
- [x] 階段3: Repository 層實作
- [x] 階段4: Service 層實作
- [x] 階段5: Controller 層實作
- [x] 階段6: 安全認證機制
- [x] 階段7: API 文件與驗證
- [x] 階段8: 外部 API 整合
- [x] 階段9: 通知系統
- [x] 階段10: Docker 容器化
- [x] 階段11: 資料庫初始化
- [x] 階段12: API 優化與權限控制
- [x] 階段13: 單元測試實作 ✅

### 當前階段：階段14 - 整合測試與部署準備

#### 學習重點
- [x] 測試架構設計
- [x] Service 層測試實作 ✅ **已完成並標準化**
- [x] Repository 層測試實作
- [x] Controller 層測試實作
- [x] 測試環境配置優化 ✅ **Docker WSL2 環境已配置**
- [x] 測試覆蓋率報告 ✅ **JaCoCo 已配置**
- [x] Service 測試最佳實踐 ✅ **已建立標準化模式**

#### 遇到的問題與解決方案
- [x] **Mockito ByteBuddy 初始化錯誤** - 在 Docker WSL2 環境中遇到 Mockito 無法初始化的問題 ✅ **已解決**
- [x] **外鍵約束自動生成** - Hibernate JPA 自動生成外鍵約束的影響 ✅ **已解決**
- [x] **測試配置缺失** - 缺少針對 Docker 環境的測試配置 ✅ **已解決**
- [x] **Service 測試標準化** - 建立統一的測試模式和最佳實踐 ✅ **已完成**
- [x] **測試資料 Mock 設定** - 正確設定 Mock 返回值避免測試資料混淆 ✅ **已解決**
- [x] **外部 API 測試** - 外部服務的 Mock 測試模式 ✅ **已建立**

#### 相關文件
- [單元測試實作指南](./13_階段13_單元測試實作.md)
- [Java 測試套件說明](../java-packages/10_單元測試套件說明.md)
- [單元測試錯誤分析報告](../docs/Unit_Test_Error_Analysis.md)

### 學習路徑

#### 基礎概念
1. [Spring Boot 基礎概念](./01_階段1_基礎架構建立.md)
2. [JPA 與資料庫設計](./02_階段2_實體設計與資料庫.md)
3. [分層架構設計](./03_階段3_Repository層實作.md)

#### 核心功能
4. [業務邏輯實作](./04_階段4_Service層實作.md)
5. [REST API 設計](./05_階段5_Controller層實作.md)
6. [安全認證機制](./06_階段6_安全認證機制.md)

#### 進階功能
7. [API 文件與驗證](./07_階段7_API文件與驗證.md)
8. [外部系統整合](./08_階段8_外部API整合.md)
9. [通知系統設計](./09_階段9_通知系統.md)

#### 部署與測試
10. [容器化部署](./10_階段10_Docker容器化.md)
11. [資料庫管理](./11_階段11_資料庫初始化.md)
12. [API 優化](./12_階段12_API優化與權限控制.md)
13. [單元測試](./13_階段13_單元測試實作.md)

### 技術棧學習

#### Spring Boot 生態
- [Spring Boot 核心概念](../spring-boot/01_Spring_Boot_核心概念.md)
- [Spring Security 安全機制](../spring-boot/02_Spring_Security_安全機制.md)
- [Spring Data JPA 資料存取](../spring-boot/03_Spring_Data_JPA_資料存取.md)
- [Spring Web MVC REST API](../spring-boot/04_Spring_Web_MVC_REST_API.md)
- [Spring Boot 測試框架](../spring-boot/05_Spring_Boot_測試框架.md)

#### Java 核心技術
- [Java 17 新特性](../java-core/01_Java_17_新特性.md)
- [JPA 與 Hibernate](../java-core/02_JPA_與_Hibernate.md)
- [Bean Validation 驗證](../java-core/03_Bean_Validation_驗證.md)
- [JWT 認證機制](../java-core/04_JWT_認證機制.md)
- [Lombok 工具使用](../java-core/05_Lombok_工具使用.md)

#### Java 套件學習
- [JUnit 5 測試框架](../java-packages/01_JUnit_5_測試框架.md)
- [Mockito 模擬框架](../java-packages/02_Mockito_模擬框架.md)
- [Spring Boot Test](../java-packages/03_Spring_Boot_Test.md)
- [H2 內存資料庫](../java-packages/04_H2_內存資料庫.md)
- [JaCoCo 程式碼覆蓋率](../java-packages/05_JaCoCo_程式碼覆蓋率.md)
- [Testcontainers 容器測試](../java-packages/06_Testcontainers_容器測試.md)
- [REST Assured API 測試](../java-packages/07_REST_Assured_API_測試.md)
- [Hamcrest 斷言庫](../java-packages/08_Hamcrest_斷言庫.md)
- [Maven Surefire 測試執行](../java-packages/09_Maven_Surefire_測試執行.md)
- [單元測試套件說明](../java-packages/10_單元測試套件說明.md)

#### 資料庫技術
- [PostgreSQL 基礎](../database/01_PostgreSQL_基礎.md)
- [資料庫設計原則](../database/02_資料庫設計原則.md)
- [SQL 查詢優化](../database/03_SQL_查詢優化.md)

#### 開發工具
- [Docker 容器化](../tools/01_Docker_容器化.md)
- [Maven 專案管理](../tools/02_Maven_專案管理.md)
- [Git 版本控制](../tools/03_Git_版本控制.md)
- [IDE 開發環境](../tools/04_IDE_開發環境.md)

### 專案實作經驗

#### 架構設計經驗
- [分層架構設計原則](../experience/01_分層架構設計原則.md)
- [RESTful API 設計規範](../experience/02_RESTful_API_設計規範.md)
- [資料庫設計最佳實踐](../experience/03_資料庫設計最佳實踐.md)

#### 開發流程經驗
- [敏捷開發流程](../experience/04_敏捷開發流程.md)
- [程式碼審查標準](../experience/05_程式碼審查標準.md)
- [測試驅動開發](../experience/06_測試驅動開發.md)

#### 部署與維護經驗
- [容器化部署策略](../experience/07_容器化部署策略.md)
- [監控與日誌管理](../experience/08_監控與日誌管理.md)
- [效能優化技巧](../experience/09_效能優化技巧.md)

### 學習資源

#### 官方文件
- [Spring Boot 官方文件](https://spring.io/projects/spring-boot)
- [Spring Security 官方文件](https://spring.io/projects/spring-security)
- [JPA 官方文件](https://jakarta.ee/specifications/persistence/)
- [JUnit 5 官方文件](https://junit.org/junit5/)

#### 最佳實踐
- [Spring Boot 最佳實踐](../best-practices/01_Spring_Boot_最佳實踐.md)
- [測試最佳實踐](../best-practices/02_測試最佳實踐.md)
- [安全最佳實踐](../best-practices/03_安全最佳實踐.md)

### 學習筆記

#### 重要概念記錄
- [重要概念筆記](./notes/01_重要概念筆記.md)
- [常見問題解決](./notes/02_常見問題解決.md)
- [效能優化筆記](./notes/03_效能優化筆記.md)

#### 程式碼範例
- [程式碼範例集](./examples/01_程式碼範例集.md)
- [設計模式應用](./examples/02_設計模式應用.md)
- [測試範例集](./examples/03_測試範例集.md)

---

## 學習進度統計

- **總學習項目**: 50+
- **已完成項目**: 45+
- **進行中項目**: 5
- **完成率**: 90%

## 下一步學習計劃

1. **完成單元測試實作**
   - 解決 Docker 環境中的測試問題
   - 實現完整的測試覆蓋率
   - 建立測試最佳實踐

2. **整合測試實作**
   - 使用 Testcontainers 進行整合測試
   - 建立端到端測試流程
   - 實現自動化測試流程

3. **效能優化**
   - 資料庫查詢優化
   - API 響應時間優化
   - 記憶體使用優化

4. **部署與監控**
   - 生產環境部署
   - 監控系統建立
   - 日誌管理系統

---

*最後更新: 2025-08-23*
