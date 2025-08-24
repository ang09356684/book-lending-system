# 單元測試錯誤分析報告

## 問題概述

在 Docker WSL2 環境中執行單元測試時，遇到嚴重的 Mockito 初始化錯誤，導致所有 Repository 測試失敗。

## 錯誤詳情

### 主要錯誤
```
java.lang.IllegalStateException: Could not initialize plugin: interface org.mockito.plugins.MockMaker (alternate: null)
```

### 根本原因
```
Could not initialize inline Byte Buddy mock maker.
It appears as if your JDK does not supply a working agent attachment mechanism.
```

### 環境信息
- **Java**: 17
- **JVM**: Eclipse Adoptium 17.0.11+9
- **OS**: Linux 6.6.87.2-microsoft-standard-WSL2
- **容器**: Docker in WSL2

## 問題分析

### 1. Mockito ByteBuddy 問題
**原因**: Mockito 的 inline mock maker 需要 JVM agent 機制來動態修改字節碼，但在 Docker WSL2 環境中，這個機制無法正常工作。

**影響範圍**:
- 所有使用 `@DataJpaTest` 的 Repository 測試
- 所有使用 Mockito 的測試
- Spring Boot 測試框架的 Mock 重置功能

### 2. 外鍵約束問題
**現象**: Hibernate 自動生成外鍵約束
```
alter table if exists borrow_records add constraint FKhqf9ceuw2x6rxawa6s1e2fs1w foreign key (book_copy_id) references book_copies
```

**原因**: Entity 類別中的 JPA 關聯註解會自動生成外鍵約束
- `@ManyToOne` + `@JoinColumn`
- `@OneToMany` + `mappedBy`

**影響**: 測試數據庫初始化時會建立外鍵約束，可能影響測試隔離性。

### 3. 測試配置問題
**當前配置缺失**:
- `pom.xml` 中缺少 Mockito 相關的 JVM 參數
- 沒有針對 Docker 環境的特殊配置
- 缺少 Testcontainers 相關依賴

## 解決方案

### 方案一：禁用 Mockito Inline Mock Maker (推薦)

#### 1. 修改 pom.xml
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.1.2</version>
    <configuration>
        <argLine>
            -Dmockito.inline.mockmaker=disabled
            --add-opens java.base/java.lang=ALL-UNNAMED
            --add-opens java.base/java.util=ALL-UNNAMED
            --add-opens java.base/java.lang.reflect=ALL-UNNAMED
        </argLine>
        <forkCount>1</forkCount>
        <reuseForks>false</reuseForks>
    </configuration>
</plugin>
```

#### 2. 添加 Mockito 配置文件
創建文件: `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker`
```
mock-maker-inline
```

### 方案二：使用 Testcontainers (替代方案)

#### 1. 添加 Testcontainers 依賴
```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

#### 2. 配置 Docker 環境
確保 `docker-compose.dev.yml` 中掛載了 Docker socket:
```yaml
volumes:
  - /var/run/docker.sock:/var/run/docker.sock
```

### 方案三：簡化測試策略

#### 1. 分層測試策略
- **Service 層**: 使用純 Mockito (無 Spring Boot)
- **Repository 層**: 使用 H2 內存數據庫
- **Controller 層**: 使用 `@WebMvcTest`

#### 2. 測試隔離
- 每個測試類使用獨立的測試配置
- 避免跨測試類的狀態共享

## 建議的測試架構

### 1. 測試分類
```
src/test/java/com/library/
├── service/           # Service 層測試 (純 Mockito)
├── repository/        # Repository 層測試 (H2 + @DataJpaTest)
├── controller/        # Controller 層測試 (@WebMvcTest)
└── integration/       # 整合測試 (@SpringBootTest + Testcontainers)
```

### 2. 測試配置
```
src/test/resources/
├── application-test.yml          # 基礎測試配置
├── application-h2.yml           # H2 數據庫配置
├── application-testcontainers.yml # Testcontainers 配置
└── mockito-extensions/          # Mockito 擴展配置
```

## 執行策略

### 階段一：修復基礎配置
1. 更新 `pom.xml` 添加 Mockito 配置
2. 創建 Mockito 擴展文件
3. 驗證 SimpleTest 能正常執行

### 階段二：逐步啟用測試
1. 先執行 Service 層測試
2. 再執行 Repository 層測試
3. 最後執行 Controller 層測試

### 階段三：整合測試
1. 配置 Testcontainers
2. 執行完整的整合測試套件

## 預期結果

修復後應該能夠：
- 成功執行所有單元測試
- 獲得正確的測試覆蓋率報告
- 在 Docker 環境中穩定運行測試

## 注意事項

1. **不要修改現有業務邏輯**: 用戶明確要求不更改已驗證的邏輯
2. **保持測試簡潔**: 只寫必要的測試，避免過度測試
3. **環境一致性**: 確保測試環境與開發環境一致
4. **性能考慮**: 測試執行時間應該合理，避免過長的等待時間

## 下一步行動

1. 實施方案一的配置修改
2. 重新執行測試驗證修復效果
3. 根據結果調整策略
4. 完成所有必要的單元測試實現

