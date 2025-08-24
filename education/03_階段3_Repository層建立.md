# 階段4: Repository層建立

## 📋 **學習目標**

本階段將學習如何建立Repository層，使用Spring Data JPA來簡化資料庫操作。

### **學習重點**
- 了解Spring Data JPA的作用
- 學習Repository介面的建立
- 使用內建的查詢方法
- 建立自定義查詢方法
- 了解Repository的生命週期

---

## 🎯 **Spring Data JPA 簡介**

### **什麼是Spring Data JPA？**
Spring Data JPA是Spring Framework的一個模組，提供了更高層次的資料存取抽象，讓我們可以用更簡單的方式操作資料庫。

### **Spring Data JPA vs 傳統JPA**
- **傳統JPA**: 需要手動寫EntityManager和SQL
- **Spring Data JPA**: 只需要定義介面，Spring自動實作

### **為什麼使用Spring Data JPA？**
- ✅ **減少樣板程式碼**: 不需要手動實作CRUD操作
- ✅ **自動生成查詢**: 根據方法名稱自動生成SQL
- ✅ **型別安全**: 編譯時檢查查詢方法
- ✅ **易於測試**: 可以輕鬆模擬Repository
- ✅ **整合Spring**: 與Spring生態系統完美整合

---

## 📝 **Repository介面建立**

### **1. 基本Repository介面**

#### **繼承JpaRepository**
```java
public interface UserRepository extends JpaRepository<User, Long> {
    // 自動擁有基本的CRUD方法
}
```

#### **JpaRepository提供的方法**
- `save(entity)` - 儲存實體
- `findById(id)` - 根據ID查詢
- `findAll()` - 查詢所有
- `delete(entity)` - 刪除實體
- `count()` - 計算數量

### **2. 查詢方法命名規則**

#### **基本查詢**
```java
// 根據欄位名稱查詢
User findByEmail(String email);
List<User> findByRole(Role role);

// 多條件查詢
List<User> findByRoleOrIsVerified(Role role, Boolean isVerified);
```

#### **查詢關鍵字**
| 關鍵字 | 說明 | 範例 |
|--------|------|------|
| `findBy` | 查詢 | `findByEmail` |
| `countBy` | 計算 | `countByRole` |
| `deleteBy` | 刪除 | `deleteByEmail` |
| `existsBy` | 存在檢查 | `existsByEmail` |

#### **條件關鍵字**
| 關鍵字 | 說明 | 範例 |
|--------|------|------|
| `And` | 且 | `findByUsernameAndEmail` |
| `Or` | 或 | `findByRoleOrIsVerified` |
| `Not` | 非 | `findByEmailNot` |
| `Like` | 模糊查詢 | `findByEmailLike` |
| `In` | 包含 | `findByRoleIn` |
| `Between` | 範圍 | `findByCreatedAtBetween` |

### **3. 排序和分頁**

#### **排序**
```java
// 單一排序
List<User> findByRoleOrderByCreatedAtDesc(Role role);

// 多欄位排序
List<User> findByRoleOrderByCreatedAtDescNameAsc(Role role);
```

#### **分頁**
```java
// 分頁查詢
Page<User> findByRole(Role role, Pageable pageable);

// 使用方式
Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
Page<User> users = userRepository.findByRole(role, pageable);
```

---

## 🔧 **自定義查詢方法**

### **1. 何時使用簡單的方法名稱查詢？**

#### **✅ 適合使用一行方法名稱的情況**
```java
// 1. 根據單一欄位查詢
Optional<Role> findByName(String name);
Optional<User> findByEmail(String email);
List<User> findByRole(Role role);

// 2. 存在性檢查
boolean existsByName(String name);
boolean existsByEmail(String email);

// 3. 計數查詢
long countByRole(Role role);
long countByCategory(String category);

// 4. 刪除操作
void deleteByEmail(String email);

// 5. 簡單的多條件查詢
List<User> findByRoleAndIsVerified(Role role, Boolean isVerified);
```

#### **為什麼RoleRepository只需要一行？**
```java
// Role實體很簡單，只有基本欄位
@Entity
public class Role {
    private Long id;
    private String name;        // 唯一欄位
    private String description; // 描述欄位
}

// 所以只需要這些基本查詢：
Optional<Role> findByName(String name);  // 根據名稱查詢
boolean existsByName(String name);       // 檢查名稱是否存在
```

### **2. 何時使用@Query註解？**

#### **❌ 不適合使用方法名稱查詢的情況**
```java
// 1. 複雜的條件邏輯
// ❌ 這樣會很長且難讀：
// List<User> findByRoleAndIsVerifiedAndCreatedAtBetweenAndUsernameLike(Role role, Boolean verified, LocalDateTime start, LocalDateTime end, String username);

// ✅ 使用@Query更清楚：
@Query("SELECT u FROM User u WHERE u.role = :role AND u.isVerified = :verified AND u.createdAt BETWEEN :startDate AND :endDate AND u.name LIKE %:name%")
List<User> findUsersByComplexCriteria(@Param("role") Role role, 
                                     @Param("verified") Boolean verified,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     @Param("name") String name);

// 2. 需要JOIN查詢
// ❌ 方法名稱無法表達JOIN：
// List<User> findByRoleName(String roleName);

// ✅ 使用@Query可以JOIN：
@Query("SELECT u FROM User u JOIN u.role r WHERE r.name = :roleName")
List<User> findUsersByRoleName(@Param("roleName") String roleName);

// 3. 需要聚合函數
// ❌ 方法名稱無法表達聚合：
// double getAverageAgeByRole(Role role);

// ✅ 使用@Query可以聚合：
@Query("SELECT AVG(u.age) FROM User u WHERE u.role = :role")
double getAverageAgeByRole(@Param("role") Role role);

// 4. 需要子查詢
// ❌ 方法名稱無法表達子查詢：
// List<User> findUsersWithMoreThanAverageBorrows();

// ✅ 使用@Query可以子查詢：
@Query("SELECT u FROM User u WHERE SIZE(u.borrowRecords) > (SELECT AVG(SIZE(u2.borrowRecords)) FROM User u2)")
List<User> findUsersWithMoreThanAverageBorrows();

// 5. 需要動態條件（可選參數）
// ❌ 方法名稱無法表達可選條件：
// List<Book> findByTitleAndAuthorAndCategory(String title, String author, String category);

// ✅ 使用@Query可以處理NULL值：
@Query("SELECT b FROM Book b WHERE " +
       "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
       "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
       "(:category IS NULL OR b.category = :category)")
List<Book> searchBooks(@Param("title") String title, 
                       @Param("author") String author, 
                       @Param("category") String category);
```

#### **使用@Query註解**

##### **JPQL查詢**
```java
@Query("SELECT u FROM User u WHERE u.role = :role AND u.isVerified = :verified")
List<User> findVerifiedUsersByRole(@Param("role") Role role, @Param("verified") Boolean verified);
```

##### **原生SQL查詢**
```java
@Query(value = "SELECT * FROM users WHERE role_id = :roleId AND is_verified = :verified", nativeQuery = true)
List<User> findVerifiedUsersByRoleId(@Param("roleId") Long roleId, @Param("verified") Boolean verified);
```

### **2. 複雜查詢範例**

#### **書籍搜尋**
```java
@Query("SELECT b FROM Book b WHERE " +
       "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
       "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
       "(:category IS NULL OR b.category = :category)")
List<Book> searchBooks(@Param("title") String title, 
                       @Param("author") String author, 
                       @Param("category") String category);
```

#### **借閱記錄查詢**
```java
@Query("SELECT br FROM BorrowRecord br WHERE " +
       "br.user.id = :userId AND " +
       "br.status = :status AND " +
       "br.dueAt < :currentDate")
List<BorrowRecord> findOverdueRecords(@Param("userId") Long userId, 
                                      @Param("status") String status, 
                                      @Param("currentDate") LocalDateTime currentDate);
```

---

## 🏗️ **建立Repository類別**

### **步驟1: 建立repository目錄**
```bash
mkdir -p src/main/java/com/library/repository
```

### **步驟2: 建立UserRepository**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 基本查詢方法
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // 複雜查詢
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isVerified = :verified")
    List<User> findVerifiedUsersByRole(@Param("role") Role role, @Param("verified") Boolean verified);
    
    // 分頁查詢
    Page<User> findByRole(Role role, Pageable pageable);
}
```

### **步驟3: 建立RoleRepository**
```java
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    // 基本查詢方法 - 只需要一行！
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}
```

### **步驟3: 建立BookRepository**
```java
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // 基本查詢方法
    List<Book> findByAuthor(String author);
    List<Book> findByCategory(String category);
    List<Book> findByPublishedYear(Integer publishedYear);
    
    // 搜尋功能
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:category IS NULL OR b.category = :category)")
    List<Book> searchBooks(@Param("title") String title, 
                           @Param("author") String author, 
                           @Param("category") String category);
    
    // 統計查詢
    long countByCategory(String category);
    long countByAuthor(String author);
}
```

### **步驟4: 建立BookCopyRepository**
```java
@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    
    // 基本查詢方法
    List<BookCopy> findByBook(Book book);
    List<BookCopy> findByLibrary(Library library);
    List<BookCopy> findByStatus(String status);
    
    // 可用副本查詢
    List<BookCopy> findByBookAndStatus(Book book, String status);
    List<BookCopy> findByLibraryAndStatus(Library library, String status);
    
    // 統計查詢
    long countByBookAndStatus(Book book, String status);
    long countByLibraryAndStatus(Library library, String status);
}
```

### **步驟5: 建立BorrowRecordRepository**
```java
@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    
    // 基本查詢方法
    List<BorrowRecord> findByUser(User user);
    List<BorrowRecord> findByBookCopy(BookCopy bookCopy);
    List<BorrowRecord> findByStatus(String status);
    
    // 逾期查詢
    @Query("SELECT br FROM BorrowRecord br WHERE " +
           "br.user.id = :userId AND " +
           "br.status = :status AND " +
           "br.dueAt < :currentDate")
    List<BorrowRecord> findOverdueRecords(@Param("userId") Long userId, 
                                          @Param("status") String status, 
                                          @Param("currentDate") LocalDateTime currentDate);
    
    // 活躍借閱查詢
    List<BorrowRecord> findByUserAndStatus(User user, String status);
    
    // 統計查詢
    long countByUserAndStatus(User user, String status);
}
```

---

## 🧪 **測試Repository**

### **建立測試類別**
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
    
    @Test
    void testExistsByEmail() {
        // Given
        User user = new User("testuser", "password", "test@example.com", "Test User", role);
        userRepository.save(user);
        
        // When
        boolean exists = userRepository.existsByEmail("test@example.com");
        
        // Then
        assertTrue(exists);
    }
}
```

---

## ⚠️ **注意事項**

### **1. 命名規範**
- Repository介面名以Repository結尾
- 方法名要符合Spring Data JPA的命名規則
- 使用Optional包裝可能為空的查詢結果

### **2. 效能考量**
- 避免N+1查詢問題
- 使用適當的FetchType
- 考慮使用@BatchSize減少查詢次數

### **3. 查詢方法設計**
- 方法名要清楚表達查詢意圖
- 參數名稱要與Entity欄位對應
- 複雜查詢使用@Query註解

### **4. 選擇查詢方式的決策樹**
```
開始
  ↓
是否需要複雜條件？ (AND/OR/範圍/模糊查詢)
  ↓
是 → 使用@Query註解
  ↓
否 → 是否需要JOIN？
  ↓
是 → 使用@Query註解
  ↓
否 → 是否需要聚合函數？ (COUNT/AVG/SUM等)
  ↓
是 → 使用@Query註解
  ↓
否 → 是否需要子查詢？
  ↓
是 → 使用@Query註解
  ↓
否 → 是否需要動態條件？ (可選參數)
  ↓
是 → 使用@Query註解
  ↓
否 → 使用簡單的方法名稱查詢 ✅
```

### **5. 實用範例對比**
```java
// ✅ 簡單查詢 - 使用方法名稱
Optional<Role> findByName(String name);
Optional<User> findByUsername(String username);
List<User> findByRole(Role role);

// ❌ 複雜查詢 - 不要用方法名稱
// List<User> findByRoleAndIsVerifiedAndCreatedAtBetweenAndUsernameLikeAndEmailContaining(...);

// ✅ 複雜查詢 - 使用@Query
@Query("SELECT u FROM User u WHERE u.role = :role AND u.isVerified = :verified AND u.createdAt BETWEEN :startDate AND :endDate")
List<User> findUsersByComplexCriteria(@Param("role") Role role, 
                                     @Param("verified") Boolean verified,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
```

### **4. 測試策略**
- 每個Repository都要有對應的測試
- 測試要涵蓋正常和異常情況
- 使用@Transactional確保測試資料隔離

---

## 📚 **下一步**

完成Repository層建立後，下一步將是：
- **階段5: Service層** - 建立業務邏輯層
- 學習Service層的設計模式
- 整合Repository和業務邏輯

---

## 🎯 **學習檢查清單**

- [ ] 了解Spring Data JPA的作用和優點
- [ ] 學會建立Repository介面
- [ ] 掌握查詢方法命名規則
- [ ] 學會使用@Query註解
- [ ] 了解何時使用方法名稱 vs @Query
- [ ] 建立所有Repository類別
- [ ] 實作自定義查詢方法
- [ ] 測試Repository功能
- [ ] 了解分頁和排序功能

---

**準備好開始建立Repository層了嗎？我們將為每個Entity建立對應的Repository介面！**
