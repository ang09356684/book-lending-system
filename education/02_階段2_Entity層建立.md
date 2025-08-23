# 階段3: Entity類別建立與JPA註解

## 📋 **學習目標**

本階段將學習如何建立Java Entity類別，並使用JPA註解將Java物件對應到資料庫表結構。

### **學習重點**
- 了解JPA (Java Persistence API) 的作用
- 學習常用的JPA註解
- 建立對應資料庫表的Entity類別
- 設定Entity之間的關聯關係

---

## 🎯 **JPA 簡介**

### **什麼是JPA？**
JPA (Java Persistence API) 是Java的ORM (Object-Relational Mapping) 標準，讓我們可以用Java物件來操作資料庫，而不需要直接寫SQL。

### **JPA vs Hibernate**
- **JPA**: 是一個標準規範
- **Hibernate**: 是JPA的一個實作，Spring Boot預設使用Hibernate

### **為什麼使用JPA？**
- ✅ **物件導向**: 用Java物件代表資料庫記錄
- ✅ **型別安全**: 編譯時檢查型別錯誤
- ✅ **減少SQL**: 大部分操作不需要手寫SQL
- ✅ **跨資料庫**: 同一套程式碼可以支援不同資料庫

---

## 📝 **常用JPA註解**

### **1. 類別級別註解**

#### **@Entity**
```java
@Entity
public class User {
    // 類別內容
}
```
- 告訴JPA這是一個Entity類別
- 對應資料庫中的一個表

#### **@Table**
```java
@Entity
@Table(name = "users")
public class User {
    // 類別內容
}
```
- 指定對應的資料庫表名
- 如果不指定，預設使用類別名

### **2. 欄位級別註解**

#### **@Id**
```java
@Id
private Long id;
```
- 標示主鍵欄位

#### **@GeneratedValue**
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```
- 設定主鍵自動生成策略
- `IDENTITY`: 使用資料庫的自動遞增
- `SEQUENCE`: 使用序列
- `TABLE`: 使用專門的表來生成ID

#### **@Column**
```java
@Column(name = "user_name", nullable = false, length = 50)
private String username;
```
- 指定欄位對應的資料庫欄位名
- 設定約束條件 (nullable, length等)

#### **@Enumerated**
```java
@Enumerated(EnumType.STRING)
private UserStatus status;
```
- 將Java enum對應到資料庫欄位
- `STRING`: 儲存enum的名稱
- `ORDINAL`: 儲存enum的序號

### **3. 關聯關係註解**

#### **@ManyToOne**
```java
@ManyToOne
@JoinColumn(name = "role_id")
private Role role;
```
- 多對一關係
- 多個User對應一個Role

#### **@OneToMany**
```java
@OneToMany(mappedBy = "user")
private List<BorrowRecord> borrowRecords;
```
- 一對多關係
- 一個User對應多個BorrowRecord

#### **@OneToOne**
```java
@OneToOne
@JoinColumn(name = "user_id")
private UserProfile profile;
```
- 一對一關係

#### **@ManyToMany**
```java
@ManyToMany
@JoinTable(
    name = "user_books",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "book_id")
)
private List<Book> favoriteBooks;
```
- 多對多關係

### **4. 時間相關註解**

#### **@CreatedDate**
```java
@CreatedDate
@Column(name = "created_at")
private LocalDateTime createdAt;
```
- 自動設定建立時間

#### **@LastModifiedDate**
```java
@LastModifiedDate
@Column(name = "updated_at")
private LocalDateTime updatedAt;
```
- 自動設定更新時間

### **5. Entity 生命週期監聽**

#### **@EntityListeners**
```java
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {
    // Entity 內容
}
```
- 監聽Entity的生命週期事件
- 自動執行對應的方法

#### **Entity 生命週期事件：**

| 事件 | 觸發時機 | 常用用途 |
|------|----------|----------|
| `@PrePersist` | 儲存前 | 設定建立時間、預設值 |
| `@PostPersist` | 儲存後 | 記錄日誌、發送通知 |
| `@PreUpdate` | 更新前 | 設定更新時間、驗證資料 |
| `@PostUpdate` | 更新後 | 記錄變更、發送通知 |
| `@PreRemove` | 刪除前 | 檢查是否可以刪除 |
| `@PostRemove` | 刪除後 | 清理相關資料 |
| `@PostLoad` | 載入後 | 解密資料、計算衍生欄位 |

#### **實際應用範例：**
```java
// 自動設定時間戳記
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    private Long id;
    
    private String name;
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

#### **在主應用程式中啟用：**
```java
@SpringBootApplication
@EnableJpaAuditing  // 啟用 JPA Auditing
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

---

## 🏗️ **建立Entity類別**

### **步驟1: 建立entity目錄**
```bash
mkdir -p src/main/java/com/library/entity
```

### **步驟2: 建立Role Entity**
```java
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    // 關聯關係
    @OneToMany(mappedBy = "role")
    private List<User> users;
    
    // 建構函式、getter、setter
}
```

### **步驟3: 建立User Entity**
```java
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)  // 啟用生命週期監聽
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;
    
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @Column(name = "librarian_id", length = 50)
    private String librarianId;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 關聯關係
    @OneToMany(mappedBy = "user")
    private List<BorrowRecord> borrowRecords;
    
    // 建構函式、getter、setter
}
```

**注意：** 使用 @CreatedDate 和 @LastModifiedDate 時，需要：
1. 在Entity類別上加上 @EntityListeners(AuditingEntityListener.class)
2. 在主應用程式類別上加上 @EnableJpaAuditing

---

## 🔗 **關聯關係設定**

### **關聯關係的選擇**
1. **@ManyToOne**: 最常用，效能最好
2. **@OneToMany**: 需要時才載入，避免N+1問題
3. **@OneToOne**: 一對一關係
4. **@ManyToMany**: 複雜，建議拆分成兩個@OneToMany

### **效能考量**
- **Lazy Loading**: 預設行為，需要時才載入關聯資料
- **Eager Loading**: 立即載入關聯資料，可能造成效能問題
- **FetchType.LAZY**: 延遲載入
- **FetchType.EAGER**: 立即載入

### **級聯操作**
```java
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
private List<BorrowRecord> borrowRecords;
```
- **CascadeType.ALL**: 所有操作都級聯
- **CascadeType.PERSIST**: 儲存時級聯
- **CascadeType.REMOVE**: 刪除時級聯

---

## 📊 **驗證註解**

### **Bean Validation**
```java
@Column(name = "email", unique = true, nullable = false, length = 100)
@Email
@NotBlank
private String email;

@Column(name = "full_name", nullable = false, length = 100)
@NotBlank
@Size(min = 2, max = 100)
private String fullName;
```

### **常用驗證註解**
- **@NotNull**: 不能為null
- **@NotBlank**: 不能為空字串
- **@Email**: 必須是有效的email格式
- **@Size**: 字串長度限制
- **@Min/@Max**: 數值範圍限制

---

## 🧪 **測試Entity**

### **建立測試類別**
```java
@SpringBootTest
class UserEntityTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testCreateUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPassword("password");
        
        User savedUser = userRepository.save(user);
        
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
    }
}
```

---

## ⚠️ **注意事項**

### **1. 命名規範**
- Entity類別名使用單數形式 (User, Book)
- 表名使用複數形式 (users, books)
- 欄位名使用camelCase

### **2. 效能考量**
- 避免過多的關聯關係
- 使用適當的FetchType
- 考慮使用@BatchSize減少N+1問題

### **3. 資料完整性**
- 在應用程式層面確保資料完整性
- 使用Bean Validation進行驗證
- 在Service層進行業務邏輯驗證

### **4. EntityListeners 使用**
- 適合用於自動化時間戳記設定
- 避免在Listener中放入複雜的業務邏輯
- 注意Listener的效能影響
- 確保Listener的行為是可預測的

---

## 📚 **下一步**

完成Entity類別建立後，下一步將是：
- **階段4: Repository層** - 建立資料存取層
- 學習Spring Data JPA的使用
- 建立自定義查詢方法

---

## 🎯 **學習檢查清單**

- [ ] 了解JPA的作用和優點
- [ ] 學會使用基本的JPA註解
- [ ] 建立所有Entity類別
- [ ] 設定正確的關聯關係
- [ ] 加入適當的驗證註解
- [ ] 了解EntityListeners的作用和使用方式
- [ ] 設定自動時間戳記功能
- [ ] 測試Entity的基本操作

---

**準備好開始建立Entity類別了嗎？我們將按照資料庫schema建立對應的Java Entity類別！**
