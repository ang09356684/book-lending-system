# Spring Data JPA 實作原理

## 🔍 **JPQL vs 原生SQL 詳細比較**

### **1. JPQL (Java Persistence Query Language)**

#### **語法特點**
```java
@Query("SELECT u FROM User u WHERE u.role = :role AND u.isVerified = :verified")
List<User> findVerifiedUsersByRole(@Param("role") Role role, @Param("verified") Boolean verified);
```

**優點：**
- ✅ **物件導向** - 使用Entity類別名稱 (`User`) 和屬性名稱 (`role`, `isVerified`)
- ✅ **型別安全** - 編譯時檢查Entity和屬性是否存在
- ✅ **跨資料庫** - 同一套JPQL可以支援PostgreSQL、MySQL、Oracle等
- ✅ **自動映射** - 自動將結果映射到Entity物件
- ✅ **關聯查詢** - 支援複雜的關聯查詢

**缺點：**
- ❌ **學習成本** - 需要學習JPQL語法
- ❌ **效能略低** - 需要轉換成原生SQL

#### **實際生成的SQL**
```sql
-- JPQL: SELECT u FROM User u WHERE u.role = :role AND u.isVerified = :verified
-- 生成的SQL:
SELECT user0_.id as id1_0_, 
       user0_.username as username2_0_, 
       user0_.email as email3_0_,
       user0_.role_id as role_id4_0_,
       user0_.is_verified as is_verified5_0_
FROM users user0_ 
WHERE user0_.role_id = ? AND user0_.is_verified = ?
```

### **2. 原生SQL (Native SQL)**

#### **語法特點**
```java
@Query(value = "SELECT * FROM users WHERE role_id = :roleId AND is_verified = :verified", nativeQuery = true)
List<User> findVerifiedUsersByRoleId(@Param("roleId") Long roleId, @Param("verified") Boolean verified);
```

**優點：**
- ✅ **直接SQL** - 使用資料庫的實際表名 (`users`) 和欄位名 (`role_id`, `is_verified`)
- ✅ **效能最佳** - 直接執行，沒有額外轉換
- ✅ **資料庫特定** - 可以使用特定資料庫的功能 (如PostgreSQL的JSON操作)
- ✅ **熟悉度** - 對SQL熟悉的開發者容易理解

**缺點：**
- ❌ **綁定資料庫** - 只能用在特定資料庫
- ❌ **型別不安全** - 編譯時無法檢查欄位是否存在
- ❌ **維護困難** - 換資料庫時需要修改SQL

#### **實際執行的SQL**
```sql
-- 直接執行的SQL:
SELECT * FROM users WHERE role_id = ? AND is_verified = ?
```

---

## 🏗️ **Spring Data JPA 實作機制**

### **1. 動態代理 (Dynamic Proxy)**

Spring Data JPA使用**動態代理**技術來實作Repository介面：

```java
// 你定義的介面
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(@Param("role") Role role);
}

// Spring自動生成的實作類別 (概念性)
public class UserRepositoryImpl implements UserRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    // 方法名稱查詢的實作
    @Override
    public Optional<User> findByUsername(String username) {
        // Spring解析方法名稱後生成的實作
        String jpql = "SELECT u FROM User u WHERE u.username = :username";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("username", username);
        
        try {
            User user = query.getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    // @Query註解的實作
    @Override
    public List<User> findByRole(Role role) {
        // 直接使用@Query中定義的JPQL
        TypedQuery<User> query = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.role = :role", User.class);
        query.setParameter("role", role);
        return query.getResultList();
    }
}
```

### **2. 方法名稱解析機制**

#### **解析規則**
```java
// 方法名稱: findByUsernameAndEmail
// 解析步驟:
// 1. find: 查詢操作
// 2. By: 分隔符
// 3. Username: 第一個屬性名稱
// 4. And: 邏輯運算符
// 5. Email: 第二個屬性名稱

// 生成的JPQL:
// SELECT u FROM User u WHERE u.username = :username AND u.email = :email
```

#### **常見的解析範例**
```java
// 基本查詢
findByUsername(String username)
// → SELECT u FROM User u WHERE u.username = :username

// 多條件查詢
findByUsernameAndEmail(String username, String email)
// → SELECT u FROM User u WHERE u.username = :username AND u.email = :email

// 模糊查詢
findByUsernameLike(String username)
// → SELECT u FROM User u WHERE u.username LIKE :username

// 排序查詢
findByRoleOrderByCreatedAtDesc(Role role)
// → SELECT u FROM User u WHERE u.role = :role ORDER BY u.createdAt DESC

// 分頁查詢
findByRole(Role role, Pageable pageable)
// → SELECT u FROM User u WHERE u.role = :role
//   加上 LIMIT 和 OFFSET
```

### **3. 實作流程詳解**

#### **啟動時期的處理**
```java
// 1. Spring掃描@Repository註解
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 2. 分析介面方法
    Optional<User> findByUsername(String username);
}

// 3. 生成代理類別
public class UserRepositoryProxy implements UserRepository {
    
    private final EntityManager entityManager;
    private final QueryMethod queryMethod;
    
    @Override
    public Optional<User> findByUsername(String username) {
        // 4. 執行查詢邏輯
        return queryMethod.execute(username);
    }
}
```

#### **執行時期的處理**
```java
// 當呼叫 findByUsername("john") 時：

// 1. 解析方法名稱
String methodName = "findByUsername";
// → 解析出: find + By + Username

// 2. 生成JPQL
String jpql = "SELECT u FROM User u WHERE u.username = :username";

// 3. 建立查詢
TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
query.setParameter("username", "john");

// 4. 執行查詢
List<User> results = query.getResultList();

// 5. 包裝結果
return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
```

---

## 🎯 **實際應用建議**

### **何時使用JPQL？**
```java
// ✅ 推薦使用JPQL的情況
@Query("SELECT u FROM User u WHERE u.role = :role AND u.isVerified = :verified")
List<User> findVerifiedUsersByRole(@Param("role") Role role, @Param("verified") Boolean verified);

// 原因：
// 1. 跨資料庫相容性
// 2. 型別安全
// 3. 物件導向查詢
// 4. 複雜關聯查詢
```

### **何時使用原生SQL？**
```java
// ✅ 推薦使用原生SQL的情況
@Query(value = "SELECT COUNT(*) FROM users WHERE created_at >= :startDate", nativeQuery = true)
long countUsersCreatedAfter(@Param("startDate") LocalDateTime startDate);

// 原因：
// 1. 簡單的統計查詢
// 2. 需要使用資料庫特定功能
// 3. 效能要求極高
// 4. 複雜的聚合查詢
```

### **何時使用方法名稱查詢？**
```java
// ✅ 推薦使用方法名稱查詢的情況
Optional<User> findByUsername(String username);
List<User> findByRole(Role role);
boolean existsByEmail(String email);

// 原因：
// 1. 簡單的CRUD操作
// 2. 標準的查詢模式
// 3. 快速開發
// 4. 易於理解和維護
```

---

## ⚡ **效能考量**

### **1. 查詢效能比較**
```java
// 方法名稱查詢 - 中等效能
findByUsername("john");

// JPQL查詢 - 中等效能
@Query("SELECT u FROM User u WHERE u.username = :username")
User findByUsername(@Param("username") String username);

// 原生SQL查詢 - 最佳效能
@Query(value = "SELECT * FROM users WHERE username = :username", nativeQuery = true)
User findByUsername(@Param("username") String username);
```

### **2. 記憶體使用比較**
```java
// 方法名稱查詢 - 需要解析和生成
// JPQL查詢 - 需要轉換
// 原生SQL查詢 - 直接執行
```

---

## 🧪 **測試範例**

### **測試不同查詢方式**
```java
@SpringBootTest
@Transactional
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testFindByUsername_MethodName() {
        // 測試方法名稱查詢
        Optional<User> user = userRepository.findByUsername("john");
        assertTrue(user.isPresent());
    }
    
    @Test
    void testFindByUsername_JPQL() {
        // 測試JPQL查詢
        Optional<User> user = userRepository.findByUsernameJPQL("john");
        assertTrue(user.isPresent());
    }
    
    @Test
    void testFindByUsername_NativeSQL() {
        // 測試原生SQL查詢
        Optional<User> user = userRepository.findByUsernameNative("john");
        assertTrue(user.isPresent());
    }
}
```

---

## 📚 **總結**

### **選擇建議**
1. **簡單查詢** → 使用方法名稱查詢
2. **複雜查詢** → 使用JPQL
3. **效能關鍵** → 使用原生SQL
4. **跨資料庫** → 使用JPQL
5. **資料庫特定功能** → 使用原生SQL

### **實作原理**
- Spring Data JPA使用動態代理實作介面
- 方法名稱會被解析成JPQL
- @Query註解直接使用定義的查詢
- 所有查詢最終都會轉換成EntityManager操作

---

**了解這些原理後，你就能更好地選擇適合的查詢方式了！**
