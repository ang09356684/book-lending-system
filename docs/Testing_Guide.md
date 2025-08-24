# 測試指南 - 最小限度單元測試
## Testing Guide - Minimum Unit Testing Requirements

---

### 📋 **目錄**
1. [測試要求概述](#測試要求概述)
2. [最小測試範圍](#最小測試範圍)
3. [Service 層測試](#service-層測試)
4. [Repository 層測試](#repository-層測試)
5. [Controller 層測試](#controller-層測試)
6. [測試覆蓋率目標](#測試覆蓋率目標)

---

## 🎯 **測試要求概述**

基於 PRD 第 6.1 節測試要求，我們需要實現：
- **單元測試覆蓋率目標**: 80%+
- **測試所有業務邏輯**
- **Mock 外部依賴**
- **驗證 HTTP 狀態碼和回應格式**

### **技術棧**
- JUnit 5
- Mockito 5.4.0 (穩定版本)
- Spring Boot Test
- H2 內存數據庫

---

## 📦 **最小測試範圍**

### **必須測試的核心功能**

#### 1. **會員管理系統**
- ✅ 用戶註冊（成功、失敗）
- ✅ 館員註冊（含外部驗證）
- ✅ 用戶查詢功能

#### 2. **書籍管理系統**
- ✅ 書籍基本 CRUD 操作
- ✅ 書籍查詢功能

#### 3. **借閱系統**
- ✅ 借書流程（含規則驗證）
- ✅ 還書流程

#### 4. **外部 API 整合**
- ✅ 館員驗證 API

---

## 🔧 **Service 層測試**

### **UserService 測試範圍**
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    // 必須測試的方法：
    // 1. registerUser() - 用戶註冊
    // 2. registerLibrarian() - 館員註冊
    // 3. findByEmail() - 查詢用戶
    // 4. findById() - 根據 ID 查詢
}
```

### **BookService 測試範圍**
```java
@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    
    // 必須測試的方法：
    // 1. createBook() - 創建書籍
    // 2. findById() - 查詢書籍
    // 3. searchBooks() - 搜索書籍
    // 4. updateBook() - 更新書籍
}
```

### **BorrowService 測試範圍**
```java
@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {
    
    // 必須測試的方法：
    // 1. borrowBook() - 借書（含規則驗證）
    // 2. returnBook() - 還書
    // 3. checkBorrowLimit() - 檢查借閱限制
}
```

### **ExternalApiService 測試範圍**
```java
@ExtendWith(MockitoExtension.class)
class ExternalApiServiceTest {
    
    // 必須測試的方法：
    // 1. verifyLibrarian() - 館員驗證
    // 2. 錯誤處理場景
}
```

---

## 🗄️ **Repository 層測試**

### **測試配置**
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    // 使用 H2 內存數據庫進行測試
}
```

### **必須測試的 Repository**
1. **UserRepository**: `findByEmail()`, `existsByEmail()`
2. **BookRepository**: `findByTitle()`, `findByAuthor()`
3. **BorrowRecordRepository**: `findByUserId()`, `findActiveRecords()`

---

## 🌐 **Controller 層測試**

### **測試配置**
```java
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
}
```

### **必須測試的端點**

#### 1. **認證 API**
- `POST /api/auth/register` - 用戶註冊
- `POST /api/auth/login` - 用戶登入

#### 2. **書籍管理 API**
- `GET /api/books` - 搜索書籍
- `POST /api/books` - 創建書籍（館員權限）

#### 3. **借閱管理 API**
- `POST /api/borrowings` - 借書
- `PUT /api/borrowings/{id}/return` - 還書

### **測試範例**
```java
@Test
void testRegisterUser_Success() throws Exception {
    // Arrange
    RegisterRequest request = new RegisterRequest("John", "john@example.com", "password123");
    User user = new User("John", "john@example.com");
    when(userService.registerUser(any(), any(), any())).thenReturn(user);

    // Act & Assert
    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value("John"));
}
```

---

## 📊 **測試覆蓋率目標**

### **覆蓋率要求**
- **整體覆蓋率**: 80%+
- **Service 層覆蓋率**: 90%+
- **Controller 層覆蓋率**: 80%+
- **Repository 層覆蓋率**: 70%+

### **執行指令**
```bash
# 執行所有測試並產生覆蓋率報告
mvn test jacoco:report

# 檢視覆蓋率報告
open target/site/jacoco/index.html
```

### **覆蓋率檢查**
```xml
<!-- pom.xml 中的 JaCoCo 配置 -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---

## 🚀 **快速測試指令**

### **運行特定測試**
```bash
# 運行單個測試類
mvn test -Dtest=UserServiceTest

# 運行特定測試方法
mvn test -Dtest=UserServiceTest#testRegisterUser_Success

# 運行所有 Service 測試
mvn test -Dtest="*ServiceTest"

# 運行所有 Controller 測試
mvn test -Dtest="*ControllerTest"
```

### **Docker 環境測試**
```bash
# 在 Docker 容器中運行測試
make test

# 運行特定測試
docker-compose -f docker-compose.dev.yml exec app-dev mvn test -Dtest=UserServiceTest
```

---

## ✅ **測試檢查清單**

### **開發完成前必須確認**
- [ ] 所有 Service 方法都有對應測試
- [ ] 所有 Controller 端點都有對應測試
- [ ] 關鍵 Repository 查詢都有測試
- [ ] 外部 API 整合有 Mock 測試
- [ ] 業務規則驗證有測試覆蓋
- [ ] 錯誤處理場景有測試
- [ ] 測試覆蓋率達到 80%+
- [ ] 所有測試都能通過

### **PRD 成功標準對應**
- ✅ **單元測試通過率**: 100%
- ✅ **基本的測試覆蓋**: 80%+
- ✅ **程式碼品質**: 測試驗證

---

## 🏷️ **最佳實踐**

### **1. 測試隔離**
```java
@BeforeEach
void setUp() {
    // 每個測試前重置 Mock
    reset(userRepository, roleRepository);
}
```

### **2. 測試數據管理**
```java
private User createTestUser() {
    User user = new User();
    user.setName("Test User");
    user.setEmail("test@example.com");
    return user;
}
```

### **3. 錯誤場景測試**
```java
@Test
void testRegisterUser_EmailAlreadyExists() {
    // 測試業務邏輯的錯誤處理
    when(userRepository.existsByEmail(anyString())).thenReturn(true);
    
    assertThrows(RuntimeException.class, () -> {
        userService.registerUser("John", "existing@example.com", "password");
    });
}
```

---

**狀態**: ✅ **完成** - 最小限度單元測試指南已建立  
**目標**: 確保 PRD 第 6.1 節測試要求達成，覆蓋率 80%+
