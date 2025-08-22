# Spring Data JPA 套件說明

## 📋 **套件概述**

### **什麼是Spring Data JPA？**
Spring Data JPA是Spring Framework的一個模組，提供了更高層次的資料存取抽象。它建立在JPA (Java Persistence API) 之上，讓我們可以用更簡單的方式操作資料庫，而不需要手動寫SQL或EntityManager程式碼。

### **主要功能**
- ✅ **自動生成CRUD操作** - 不需要手動實作基本的增刪改查
- ✅ **方法名稱查詢** - 根據方法名稱自動生成SQL查詢
- ✅ **自定義查詢** - 支援JPQL和原生SQL查詢
- ✅ **分頁和排序** - 內建分頁和排序功能
- ✅ **型別安全** - 編譯時檢查查詢方法

---

## 🎯 **核心概念**

### **1. Repository模式**
Repository模式是一種設計模式，將資料存取邏輯封裝在一個介面中，讓業務邏輯層不需要直接操作資料庫。

```java
// 傳統方式 - 需要手動實作
public class UserDAO {
    public User findById(Long id) {
        // 手動寫SQL和EntityManager程式碼
    }
}

// Spring Data JPA方式 - 只需要定義介面
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring自動實作所有方法
}
```

### **2. 繼承層次結構**
```
Repository (標記介面)
    ↓
CrudRepository (基本CRUD操作)
    ↓
PagingAndSortingRepository (分頁和排序)
    ↓
JpaRepository (JPA特定功能)
```

---

## 📝 **常用註解**

### **@Repository**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Repository內容
}
```
- 標示這是一個Repository介面
- Spring會自動掃描並註冊為Bean

### **@Query**
```java
@Query("SELECT u FROM User u WHERE u.role = :role")
List<User> findByRole(@Param("role") Role role);
```
- 自定義查詢方法
- 支援JPQL和原生SQL

### **@Param**
```java
@Query("SELECT u FROM User u WHERE u.username = :username AND u.email = :email")
User findByUsernameAndEmail(@Param("username") String username, @Param("email") String email);
```
- 綁定查詢參數
- 讓參數名稱與查詢中的變數對應

---

## 🔧 **查詢方法命名規則**

### **基本格式**
```
[動作][By][屬性名][條件][OrderBy][排序]
```

### **動作關鍵字**
| 關鍵字 | 說明 | 範例 |
|--------|------|------|
| `find` | 查詢 | `findByUsername` |
| `count` | 計算 | `countByRole` |
| `delete` | 刪除 | `deleteByUsername` |
| `exists` | 存在檢查 | `existsByEmail` |

### **條件關鍵字**
| 關鍵字 | 說明 | 範例 |
|--------|------|------|
| `And` | 且 | `findByUsernameAndEmail` |
| `Or` | 或 | `findByRoleOrIsVerified` |
| `Not` | 非 | `findByUsernameNot` |
| `Like` | 模糊查詢 | `findByUsernameLike` |
| `In` | 包含 | `findByRoleIn` |
| `Between` | 範圍 | `findByCreatedAtBetween` |
| `LessThan` | 小於 | `findByAgeLessThan` |
| `GreaterThan` | 大於 | `findByAgeGreaterThan` |

### **實際範例**
```java
// 基本查詢
User findByUsername(String username);
User findByEmail(String email);

// 多條件查詢
User findByUsernameAndEmail(String username, String email);
List<User> findByRoleOrIsVerified(Role role, Boolean isVerified);

// 模糊查詢
List<User> findByUsernameLike(String username);
List<User> findByEmailContaining(String email);

// 範圍查詢
List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
List<User> findByAgeGreaterThan(Integer age);

// 排序查詢
List<User> findByRoleOrderByCreatedAtDesc(Role role);
List<User> findByRoleOrderByCreatedAtDescUsernameAsc(Role role);
```

---

## 📊 **分頁和排序**

### **分頁查詢**
```java
// Repository方法
Page<User> findByRole(Role role, Pageable pageable);

// 使用方式
Pageable pageable = PageRequest.of(0, 10); // 第0頁，每頁10筆
Page<User> users = userRepository.findByRole(role, pageable);

// 取得結果
List<User> userList = users.getContent(); // 當前頁的資料
long totalElements = users.getTotalElements(); // 總筆數
int totalPages = users.getTotalPages(); // 總頁數
boolean hasNext = users.hasNext(); // 是否有下一頁
```

### **排序查詢**
```java
// 單一排序
Sort sort = Sort.by("createdAt").descending();
List<User> users = userRepository.findByRole(role, sort);

// 多欄位排序
Sort sort = Sort.by("createdAt").descending().and(Sort.by("username").ascending());
List<User> users = userRepository.findByRole(role, sort);

// 分頁 + 排序
Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
Page<User> users = userRepository.findByRole(role, pageable);
```

---

## 🎨 **自定義查詢**

### **JPQL查詢**
```java
@Query("SELECT u FROM User u WHERE u.role = :role AND u.isVerified = :verified")
List<User> findVerifiedUsersByRole(@Param("role") Role role, @Param("verified") Boolean verified);
```

### **原生SQL查詢**
```java
@Query(value = "SELECT * FROM users WHERE role_id = :roleId AND is_verified = :verified", nativeQuery = true)
List<User> findVerifiedUsersByRoleId(@Param("roleId") Long roleId, @Param("verified") Boolean verified);
```

### **複雜查詢範例**
```java
@Query("SELECT u FROM User u WHERE " +
       "(:username IS NULL OR u.username LIKE %:username%) AND " +
       "(:email IS NULL OR u.email LIKE %:email%) AND " +
       "(:role IS NULL OR u.role = :role)")
List<User> searchUsers(@Param("username") String username, 
                       @Param("email") String email, 
                       @Param("role") Role role);
```

---

## ⚡ **效能優化**

### **1. 避免N+1查詢問題**
```java
// 問題：會產生N+1查詢
List<User> users = userRepository.findAll();
for (User user : users) {
    System.out.println(user.getRole().getName()); // 每個user都會查詢一次role
}

// 解決：使用JOIN FETCH
@Query("SELECT u FROM User u JOIN FETCH u.role")
List<User> findAllWithRole();
```

### **2. 使用@BatchSize**
```java
@Entity
@Table(name = "users")
@BatchSize(size = 10) // 一次載入10筆關聯資料
public class User {
    @OneToMany(mappedBy = "user")
    private List<BorrowRecord> borrowRecords;
}
```

### **3. 投影查詢**
```java
// 只查詢需要的欄位
@Query("SELECT u.username, u.email FROM User u WHERE u.role = :role")
List<Object[]> findUsernameAndEmailByRole(@Param("role") Role role);

// 使用DTO
public interface UserSummary {
    String getUsername();
    String getEmail();
}

@Query("SELECT u.username as username, u.email as email FROM User u WHERE u.role = :role")
List<UserSummary> findUserSummaryByRole(@Param("role") Role role);
```

---

## 🧪 **測試Repository**

### **基本測試**
```java
@SpringBootTest
@Transactional
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testFindByUsername() {
        // Given
        User user = new User("testuser", "password", "test@example.com", "Test User", role);
        userRepository.save(user);
        
        // When
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        
        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }
}
```

### **測試注意事項**
- 使用 `@Transactional` 確保測試資料隔離
- 每個測試方法都要清理測試資料
- 測試要涵蓋正常和異常情況

---

## 🎯 **在本專案中的應用**

### **UserRepository**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
}
```

### **BookRepository**
```java
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAuthor(String author);
    List<Book> findByCategory(String category);
    
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%')))")
    List<Book> searchBooks(@Param("title") String title, @Param("author") String author);
}
```

---

## ⚠️ **注意事項**

### **優點**
- ✅ 大幅減少樣板程式碼
- ✅ 自動生成查詢方法
- ✅ 型別安全的查詢
- ✅ 易於測試和維護
- ✅ 與Spring生態系統完美整合

### **缺點**
- ❌ 學習曲線較陡
- ❌ 複雜查詢可能效能不佳
- ❌ 方法名稱可能變得很長
- ❌ 除錯較困難（自動生成的SQL）

### **最佳實踐**
1. **方法命名要清楚** - 讓方法名稱清楚表達查詢意圖
2. **使用Optional** - 對於可能為空的查詢結果使用Optional
3. **複雜查詢用@Query** - 複雜查詢使用@Query註解而不是長方法名
4. **注意效能** - 避免N+1查詢問題
5. **充分測試** - 每個Repository都要有對應的測試

---

## 📚 **相關資源**

- [Spring Data JPA官方文件](https://spring.io/projects/spring-data-jpa)
- [Spring Data JPA參考指南](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [JPA查詢方法命名規則](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)

---

**Spring Data JPA讓我們可以用更簡單的方式操作資料庫，大幅提升開發效率！**
