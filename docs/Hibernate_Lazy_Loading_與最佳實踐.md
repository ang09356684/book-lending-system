# Hibernate Lazy Loading 與最佳實踐

## 目錄
1. [Lazy Loading 問題概述](#lazy-loading-問題概述)
2. [常見的 Lazy Loading 錯誤](#常見的-lazy-loading-錯誤)
3. [解決方案](#解決方案)
4. [最佳實踐](#最佳實踐)
5. [實體關聯配置](#實體關聯配置)
6. [查詢優化](#查詢優化)
7. [常見陷阱與注意事項](#常見陷阱與注意事項)
8. [本專案中的實際案例](#本專案中的實際案例)

---

## Lazy Loading 問題概述

### 什麼是 Lazy Loading？
Lazy Loading（懶加載）是 Hibernate 的預設行為，當訪問實體關聯時才會從資料庫載入相關資料。

### 為什麼會出現 Lazy Loading 問題？
- **Session 已關閉**：當 Hibernate Session 關閉後，嘗試訪問未載入的關聯會拋出異常
- **事務邊界**：在事務外訪問關聯實體
- **代理對象**：Hibernate 創建的代理對象在 Session 關閉後無法初始化

---

## 常見的 Lazy Loading 錯誤

### 1. LazyInitializationException
```java
// 錯誤示例
@GetMapping("/user/{id}")
public UserResponse getUser(@PathVariable Long id) {
    User user = userRepository.findById(id).orElseThrow();
    // 此時 Session 已關閉
    String roleName = user.getRole().getName(); // ❌ LazyInitializationException
    return new UserResponse(user.getId(), user.getName(), user.getEmail(), roleName);
}
```

### 2. 錯誤訊息
```
org.hibernate.LazyInitializationException: 
could not initialize proxy [com.library.entity.Role#2] - no Session
```

---

## 解決方案

### 方案一：使用 EAGER 載入（推薦用於頻繁訪問的關聯）

```java
@Entity
public class User {
    @ManyToOne(fetch = FetchType.EAGER)  // 改為 EAGER
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
```

**優點：**
- 自動載入關聯，無需額外查詢
- 避免 Lazy Loading 異常
- 適合頻繁訪問的關聯

**缺點：**
- 可能載入不需要的資料
- 增加記憶體使用

### 方案二：使用 JOIN FETCH 查詢

```java
// Repository
@Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
Optional<User> findByEmailWithRole(@Param("email") String email);

// Service
public Optional<User> findByEmailWithRole(String email) {
    return userRepository.findByEmailWithRole(email);
}

// Controller
User user = userService.findByEmailWithRole(email)
    .orElseThrow(() -> new RuntimeException("User not found"));
```

**優點：**
- 精確控制載入時機
- 避免 N+1 查詢問題
- 保持 LAZY 的彈性

### 方案三：使用 @EntityGraph

```java
@Entity
@NamedEntityGraph(
    name = "User.withRole",
    attributeNodes = @NamedAttributeNode("role")
)
public class User {
    // ...
}

// Repository
@Query("SELECT u FROM User u WHERE u.email = :email")
@EntityGraph("User.withRole")
Optional<User> findByEmailWithRole(@Param("email") String email);
```

### 方案四：在事務中處理

```java
@Transactional(readOnly = true)
public UserResponse getUserWithRole(Long id) {
    User user = userRepository.findById(id).orElseThrow();
    // 在事務中訪問關聯是安全的
    String roleName = user.getRole().getName();
    return new UserResponse(user.getId(), user.getName(), user.getEmail(), roleName);
}
```

---

## 最佳實踐

### 1. 關聯載入策略選擇

| 關聯類型 | 推薦策略 | 原因 |
|---------|---------|------|
| 一對一 | EAGER | 通常總是需要 |
| 多對一 | EAGER | 避免頻繁的額外查詢 |
| 一對多 | LAZY + JOIN FETCH | 避免載入大量資料 |
| 多對多 | LAZY + JOIN FETCH | 避免載入大量資料 |

### 2. 查詢方法命名

```java
// 好的命名
findByEmailWithRole()
findByIdWithBorrowRecords()
findAllWithLibrary()

// 避免的命名
findByEmail() // 不清楚是否載入關聯
```

### 3. 服務層設計

```java
@Service
@Transactional
public class UserService {
    
    // 基本查詢 - 不載入關聯
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // 載入關聯的查詢 - 明確命名
    public Optional<User> findByEmailWithRole(String email) {
        return userRepository.findByEmailWithRole(email);
    }
    
    // 業務方法 - 在事務中處理
    public UserResponse getUserProfile(Long id) {
        User user = userRepository.findByIdWithRole(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().getName());
    }
}
```

---

## 實體關聯配置

### 1. 基本配置

```java
@Entity
public class User {
    
    // 一對多關聯 - 使用 LAZY
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<BorrowRecord> borrowRecords;
    
    // 多對一關聯 - 使用 EAGER
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    // 一對一關聯 - 使用 EAGER
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private UserProfile profile;
}
```

### 2. 級聯操作

```java
@Entity
public class Book {
    
    // 級聯保存和刪除
    @OneToMany(mappedBy = "book", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<BookCopy> copies;
    
    // 級聯保存
    @OneToMany(mappedBy = "book", cascade = CascadeType.PERSIST)
    private List<BookReview> reviews;
}
```

---

## 查詢優化

### 1. 避免 N+1 查詢問題

```java
// ❌ 錯誤 - N+1 查詢
List<User> users = userRepository.findAll();
for (User user : users) {
    System.out.println(user.getRole().getName()); // 每個用戶都會查詢一次 role
}

// ✅ 正確 - 使用 JOIN FETCH
@Query("SELECT u FROM User u JOIN FETCH u.role")
List<User> findAllWithRole();
```

### 2. 分頁查詢

```java
// 分頁查詢時避免使用 JOIN FETCH
@Query("SELECT u FROM User u")
Page<User> findAllWithPagination(Pageable pageable);

// 如果需要關聯，使用 @EntityGraph
@Query("SELECT u FROM User u")
@EntityGraph("User.withRole")
Page<User> findAllWithRoleAndPagination(Pageable pageable);
```

### 3. 條件查詢

```java
// 動態查詢
@Query("SELECT u FROM User u " +
       "JOIN FETCH u.role " +
       "WHERE (:name IS NULL OR u.name LIKE %:name%) " +
       "AND (:roleName IS NULL OR u.role.name = :roleName)")
List<User> findUsersWithFilters(@Param("name") String name, @Param("roleName") String roleName);
```

---

## 常見陷阱與注意事項

### 1. 事務邊界

```java
// ❌ 錯誤 - 在事務外訪問關聯
@GetMapping("/users")
public List<UserResponse> getUsers() {
    List<User> users = userService.findAll(); // 事務已結束
    return users.stream()
        .map(user -> new UserResponse(user.getId(), user.getName(), user.getRole().getName())) // ❌ 異常
        .collect(Collectors.toList());
}

// ✅ 正確 - 在事務中處理
@Transactional(readOnly = true)
public List<UserResponse> getAllUsersWithRole() {
    List<User> users = userRepository.findAllWithRole();
    return users.stream()
        .map(user -> new UserResponse(user.getId(), user.getName(), user.getRole().getName()))
        .collect(Collectors.toList());
}
```

### 2. 循環引用

```java
// ❌ 錯誤 - 循環引用
@Entity
public class User {
    @OneToMany(mappedBy = "user")
    private List<BorrowRecord> borrowRecords;
}

@Entity
public class BorrowRecord {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

// ✅ 正確 - 使用 @JsonManagedReference 和 @JsonBackReference
@Entity
public class User {
    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<BorrowRecord> borrowRecords;
}

@Entity
public class BorrowRecord {
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
}
```

### 3. 記憶體洩漏

```java
// ❌ 錯誤 - 載入大量資料到記憶體
@Query("SELECT u FROM User u JOIN FETCH u.borrowRecords")
List<User> findAllWithBorrowRecords(); // 可能載入數萬筆資料

// ✅ 正確 - 使用分頁或流式處理
@Query("SELECT u FROM User u")
Page<User> findAllWithPagination(Pageable pageable);

// 或使用流式查詢
@Query("SELECT u FROM User u")
Stream<User> findAllAsStream();
```

---

## 本專案中的實際案例

### 1. User 實體的 Role 關聯

**問題：**
```java
// 原始配置
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "role_id", nullable = false)
private Role role;
```

**解決方案：**
```java
// 修改為 EAGER 載入
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "role_id", nullable = false)
private Role role;
```

**原因：**
- Role 資訊在大多數業務場景中都需要
- 避免頻繁的額外查詢
- 簡化代碼邏輯

### 2. 查詢方法優化

**原始方法：**
```java
// 可能出現 Lazy Loading 問題
public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
}
```

**優化後：**
```java
// 保留基本查詢
public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
}

// 新增載入關聯的查詢
public Optional<User> findByEmailWithRole(String email) {
    return userRepository.findByEmailWithRole(email);
}
```

### 3. Controller 層處理

**錯誤示例：**
```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
    User user = userService.findById(id); // 可能出現 Lazy Loading 問題
    UserResponse userResponse = new UserResponse(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getRole().getName() // ❌ 可能拋出異常
    );
    return ResponseEntity.ok(ApiResponse.success(userResponse));
}
```

**正確示例：**
```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
    User user = userService.findByIdWithRole(id); // 使用載入關聯的查詢
    UserResponse userResponse = new UserResponse(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getRole().getName() // ✅ 安全訪問
    );
    return ResponseEntity.ok(ApiResponse.success(userResponse));
}
```

---

## 總結

### 關鍵要點

1. **理解 Lazy Loading 機制**：知道何時會觸發額外查詢
2. **選擇合適的載入策略**：EAGER vs LAZY + JOIN FETCH
3. **注意事務邊界**：確保在正確的上下文中訪問關聯
4. **優化查詢效能**：避免 N+1 查詢問題
5. **明確命名**：讓方法名稱清楚表達是否載入關聯

### 檢查清單

- [ ] 所有實體關聯的 fetch 策略是否合適？
- [ ] 是否避免了 N+1 查詢問題？
- [ ] 事務邊界是否正確？
- [ ] 查詢方法命名是否清楚？
- [ ] 是否處理了循環引用？
- [ ] 大量資料查詢是否使用了分頁？

### 推薦工具

1. **Hibernate 統計資訊**：監控查詢次數
2. **Spring Boot Actuator**：監控應用效能
3. **JPA Buddy**：IDE 插件，幫助分析查詢
4. **Hibernate Types**：增強類型支援

---

*最後更新：2025-01-23*
*版本：1.0.0*
