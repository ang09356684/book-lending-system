# Spring Boot 專案結構
## 標準Java Spring Boot專案目錄結構

---

### 📁 **標準Spring Boot專案結構**

```
library-system/
├── 📚 docs/                          # 文件資料夾
│   ├── PRD_線上圖書借閱系統_輕量版.md
│   ├── TODO_開發清單.md
│   ├── Database_Design_Document.md
│   ├── README_跨平台開發.md
│   └── Spring_Boot_專案結構.md       # 本文件
├── 🔧 src/                           # 原始碼目錄
│   ├── main/                         # 主要程式碼
│   │   ├── java/                     # Java原始碼
│   │   │   └── com/
│   │   │       └── library/          # 基礎套件
│   │   │           ├── LibraryApplication.java  # 主啟動類別
│   │   │           ├── config/       # 配置類別
│   │   │           │   ├── SecurityConfig.java
│   │   │           │   ├── DatabaseConfig.java
│   │   │           │   └── OpenApiConfig.java
│   │   │           ├── controller/   # 控制器層
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── BookController.java
│   │   │           │   └── BorrowController.java
│   │   │           ├── service/      # 服務層
│   │   │           │   ├── UserService.java
│   │   │           │   ├── BookService.java
│   │   │           │   ├── BorrowService.java
│   │   │           │   └── NotificationService.java
│   │   │           ├── repository/   # 資料存取層
│   │   │           │   ├── UserRepository.java
│   │   │           │   ├── BookRepository.java
│   │   │           │   ├── BookCopyRepository.java
│   │   │           │   ├── BorrowRecordRepository.java
│   │   │           │   └── LibraryRepository.java
│   │   │           ├── entity/       # 實體類別
│   │   │           │   ├── User.java
│   │   │           │   ├── Role.java
│   │   │           │   ├── Book.java
│   │   │           │   ├── BookCopy.java
│   │   │           │   ├── Library.java
│   │   │           │   ├── BorrowRecord.java
│   │   │           │   └── Notification.java
│   │   │           ├── dto/          # 資料傳輸物件
│   │   │           │   ├── UserDto.java
│   │   │           │   ├── BookDto.java
│   │   │           │   ├── BorrowDto.java
│   │   │           │   └── LoginRequest.java
│   │   │           ├── exception/    # 例外處理
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   ├── BookNotFoundException.java
│   │   │           │   └── UserNotFoundException.java
│   │   │           ├── util/         # 工具類別
│   │   │           │   ├── JwtUtil.java
│   │   │           │   └── PasswordUtil.java
│   │   │           └── external/     # 外部API整合
│   │   │               └── LibrarianVerificationService.java
│   │   └── resources/                # 資源檔案
│   │       ├── application.yml       # 主要配置
│   │       ├── application-dev.yml   # 開發環境配置
│   │       ├── application-test.yml  # 測試環境配置
│   │       ├── application-prod.yml  # 生產環境配置
│   │       ├── static/               # 靜態資源
│   │       └── templates/            # 模板檔案
│   └── test/                         # 測試程式碼
│       ├── java/                     # Java測試原始碼
│       │   └── com/
│       │       └── library/
│       │           ├── controller/   # 控制器測試
│       │           │   ├── AuthControllerTest.java
│       │           │   ├── BookControllerTest.java
│       │           │   └── BorrowControllerTest.java
│       │           ├── service/      # 服務層測試
│       │           │   ├── UserServiceTest.java
│       │           │   ├── BookServiceTest.java
│       │           │   └── BorrowServiceTest.java
│       │           ├── repository/   # 資料存取層測試
│       │           │   ├── UserRepositoryTest.java
│       │           │   ├── BookRepositoryTest.java
│       │           │   └── BorrowRecordRepositoryTest.java
│       │           └── integration/  # 整合測試
│       │               └── LibrarySystemIntegrationTest.java
│       └── resources/                # 測試資源
│           ├── application-test.yml  # 測試配置
│           └── data.sql              # 測試資料
├── 🗄️ database_schema.sql            # 資料庫Schema
├── 🐳 Dockerfile.dev                 # 開發環境Dockerfile
├── 🐳 docker-compose.dev.yml         # 開發環境Docker Compose
├── 📦 pom.xml                        # Maven配置
├── 📖 README.md                      # 主要說明文件
└── 🚫 .gitignore                     # Git忽略檔案
```

---

### 🏗️ **各層級說明**

#### **1. Controller層 (控制器層)**
- **職責**: 處理HTTP請求，接收參數，返回回應
- **位置**: `src/main/java/com/library/controller/`
- **範例**: `AuthController.java`, `BookController.java`

#### **2. Service層 (服務層)**
- **職責**: 業務邏輯處理，資料驗證，業務規則
- **位置**: `src/main/java/com/library/service/`
- **範例**: `UserService.java`, `BookService.java`

#### **3. Repository層 (資料存取層)**
- **職責**: 資料庫操作，CRUD操作
- **位置**: `src/main/java/com/library/repository/`
- **範例**: `UserRepository.java`, `BookRepository.java`

#### **4. Entity層 (實體層)**
- **職責**: 資料庫表對應的Java物件
- **位置**: `src/main/java/com/library/entity/`
- **範例**: `User.java`, `Book.java`

#### **5. DTO層 (資料傳輸物件)**
- **職責**: API請求/回應的資料結構
- **位置**: `src/main/java/com/library/dto/`
- **範例**: `UserDto.java`, `BookDto.java`

---

### 📋 **檔案命名規範**

#### **Java類別命名**
- **Controller**: `XxxController.java`
- **Service**: `XxxService.java`
- **Repository**: `XxxRepository.java`
- **Entity**: `Xxx.java` (單數形式)
- **DTO**: `XxxDto.java`
- **Exception**: `XxxException.java`
- **Config**: `XxxConfig.java`

#### **套件命名**
- **基礎套件**: `com.library`
- **子套件**: 小寫，用點分隔
- **範例**: `com.library.controller`, `com.library.service`

---

### ⚙️ **配置檔案**

#### **application.yml 結構**
```yaml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:postgresql://localhost:5432/library
    username: postgres
    password: password
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true

server:
  port: 8080

logging:
  level:
    com.library: DEBUG
```

#### **環境配置**
- **application-dev.yml**: 開發環境
- **application-test.yml**: 測試環境
- **application-prod.yml**: 生產環境

---

### 🧪 **測試結構**

#### **測試類型**
- **單元測試**: 測試單一方法或類別
- **整合測試**: 測試多個組件整合
- **端到端測試**: 測試完整流程

#### **測試命名**
- **測試類別**: `XxxTest.java`
- **測試方法**: `testXxx()`

---

### 📦 **Maven結構**

#### **pom.xml 主要區塊**
```xml
<project>
    <groupId>com.library</groupId>
    <artifactId>library-system</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <spring-boot.version>3.2.0</spring-boot.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Starters -->
        <!-- Database -->
        <!-- Security -->
        <!-- Testing -->
    </dependencies>
    
    <build>
        <plugins>
            <!-- Spring Boot Maven Plugin -->
        </plugins>
    </build>
</project>
```

---

### 🎯 **建立順序建議**

1. **建立基礎結構**
   - 建立目錄結構
   - 建立pom.xml
   - 建立主啟動類別

2. **建立Entity層**
   - 建立所有實體類別
   - 設定JPA註解

3. **建立Repository層**
   - 建立Repository介面
   - 設定查詢方法

4. **建立Service層**
   - 建立業務邏輯
   - 實作業務規則

5. **建立Controller層**
   - 建立API端點
   - 設定RESTful API

6. **建立測試**
   - 建立單元測試
   - 建立整合測試

---

**這個結構遵循Spring Boot最佳實踐，適合企業級開發！**
