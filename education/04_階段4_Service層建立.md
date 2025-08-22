# 階段5: Service層建立

## 📋 **學習目標**

本階段將學習如何建立Service層，實作業務邏輯，整合Repository層的資料存取。

### **學習重點**
- 了解Service層的作用和設計原則
- 學習Service層的建立方式
- 實作業務邏輯和驗證
- 處理異常和錯誤
- 整合Repository層

---

## 🎯 **Service層 簡介**

### **什麼是Service層？**
Service層是業務邏輯層，負責處理複雜的業務規則，整合Repository層的資料存取，並為Controller層提供服務。

### **Service層 vs Repository層**
- **Repository層**: 負責資料存取，不包含業務邏輯
- **Service層**: 負責業務邏輯，整合多個Repository

### **為什麼需要Service層？**
- ✅ **業務邏輯集中化**: 將業務規則集中在Service層
- ✅ **資料一致性**: 確保資料操作的原子性
- ✅ **程式碼重用**: 多個Controller可以共用Service
- ✅ **測試便利**: 可以獨立測試業務邏輯
- ✅ **維護性**: 業務邏輯變更時只需要修改Service

---

## 📝 **Service層設計原則**

### **1. 註解說明**

#### **@Service 註解**
```java
@Service
public class UserService {
    // 標記為Spring Bean，讓Spring容器管理
    // 自動掃描並註冊到Spring容器
    // 其他類別可以注入這個Service
}
```

#### **@Transactional 註解**
```java
@Service
@Transactional
public class BorrowService {
    
    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookCopyId) {
        // 這個方法的所有操作都在同一個事務中
        // 如果任何步驟失敗，整個操作都會回滾
        // 確保資料庫的一致性
    }
}
```

### **2. 事務管理詳解**

#### **什麼是事務？**
事務是一組原子性的資料庫操作，具有ACID特性：
- **A (Atomicity)**: 原子性 - 要麼全部成功，要麼全部失敗
- **C (Consistency)**: 一致性 - 資料庫狀態保持一致
- **I (Isolation)**: 隔離性 - 事務之間互不干擾
- **D (Durability)**: 持久性 - 一旦提交就永久保存

#### **多個SQL在同一個事務中的範例**
```java
@Service
@Transactional
public class BorrowService {
    
    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookCopyId) {
        // 所有這些操作都在同一個事務中
        // 1. 查找使用者
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 2. 查找書籍副本
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
            .orElseThrow(() -> new RuntimeException("Book copy not found"));
        
        // 3. 檢查可用性
        if (!"AVAILABLE".equals(bookCopy.getStatus())) {
            throw new RuntimeException("Book is not available");
        }
        
        // 4. 建立借閱記錄
        BorrowRecord borrowRecord = new BorrowRecord(user, bookCopy, LocalDateTime.now().plusDays(30));
        
        // 5. 更新書籍狀態
        bookCopy.setStatus("BORROWED");
        bookCopyRepository.save(bookCopy);
        
        // 6. 儲存借閱記錄
        return borrowRecordRepository.save(borrowRecord);
        
        // 如果任何步驟失敗，所有操作都會回滾
        // 例如：如果步驟6失敗，步驟5的更新也會被回滾
    }
}
```

#### **事務傳播行為**
```java
@Service
@Transactional
public class BorrowService {
    
    // 預設傳播行為：REQUIRED
    @Transactional(propagation = Propagation.REQUIRED)
    public BorrowRecord borrowBook(Long userId, Long bookCopyId) {
        // 如果當前沒有事務，創建新事務
        // 如果當前有事務，加入現有事務
    }
    
    // 總是創建新事務
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotification(User user, BorrowRecord record) {
        // 總是創建新事務，不依賴外部事務
    }
    
    // 支援事務，但不強制
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<BorrowRecord> getUserBorrows(Long userId) {
        // 如果有事務就使用，沒有也沒關係
    }
}
```

### **3. 單一職責原則**
```java
// ✅ 好的設計 - 每個Service專注於特定領域
public class UserService {
    // 只處理使用者相關的業務邏輯
}

public class BookService {
    // 只處理書籍相關的業務邏輯
}

public class BorrowService {
    // 只處理借閱相關的業務邏輯
}
```

### **4. 依賴注入**
```java
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    // 使用建構函式注入
    public UserService(UserRepository userRepository, 
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
}
```

### **5. 事務管理**
```java
@Service
@Transactional
public class BorrowService {
    
    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookCopyId) {
        // 借書邏輯 - 確保資料一致性
        // 1. 檢查使用者資格
        // 2. 檢查書籍可用性
        // 3. 建立借閱記錄
        // 4. 更新書籍狀態
        // 如果任何步驟失敗，整個操作都會回滾
    }
}
```

### **4. 依賴注入方式對比**
```java
// ❌ 方式1: 欄位注入 (不推薦)
@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository; // 可以修改，不安全
}

// ❌ 方式2: Setter注入 (不推薦)
@Service
public class BookService {
    private BookRepository bookRepository;
    
    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository; // 可以修改，不安全
    }
}

// ✅ 方式3: 建構函式注入 (推薦)
@Service
public class BookService {
    private final BookRepository bookRepository; // 不可修改，安全
    
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}
```

### **5. 為什麼使用建構函式注入？**
- **強制性**: 確保所有依賴都被提供
- **不可變性**: `final` 確保依賴不會被修改
- **線程安全**: 不可變的依賴是線程安全的
- **測試便利**: 容易進行單元測試
- **循環依賴檢測**: 編譯時就能發現循環依賴

---

## 🔧 **Service層建立**

### **1. 基本Service結構**

#### **UserService範例**
```java
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, 
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // 業務邏輯方法
    public User registerUser(UserRegistrationRequest request) {
        // 1. 驗證輸入資料
        validateRegistrationRequest(request);
        
        // 2. 檢查使用者是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        
        // 3. 取得預設角色
        Role defaultRole = roleRepository.findByName("USER")
            .orElseThrow(() -> new RoleNotFoundException("Default role not found"));
        
        // 4. 建立使用者
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(defaultRole);
        user.setIsVerified(false);
        
        // 5. 儲存使用者
        return userRepository.save(user);
    }
    
    public User authenticateUser(String username, String password) {
        // 1. 查找使用者
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // 2. 驗證密碼
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }
        
        // 3. 檢查使用者狀態
        if (!user.getIsVerified()) {
            throw new UserNotVerifiedException("User not verified");
        }
        
        return user;
    }
    
    private void validateRegistrationRequest(UserRegistrationRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        
        if (request.getEmail() == null || !isValidEmail(request.getEmail())) {
            throw new ValidationException("Valid email is required");
        }
        
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
```

### **2. 業務邏輯實作**

#### **BookService範例**
```java
@Service
@Transactional
public class BookService {
    
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    
    public BookService(BookRepository bookRepository, 
                      BookCopyRepository bookCopyRepository) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
    }
    
    public Book createBook(BookCreationRequest request) {
        // 1. 驗證輸入資料
        validateBookRequest(request);
        
        // 2. 建立書籍
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublishedYear(request.getPublishedYear());
        book.setCategory(request.getCategory());
        
        // 3. 儲存書籍
        Book savedBook = bookRepository.save(book);
        
        // 4. 建立書籍副本
        createBookCopies(savedBook, request.getLibraryId(), request.getCopyCount());
        
        return savedBook;
    }
    
    public List<Book> searchBooks(BookSearchRequest request) {
        // 1. 執行搜尋
        List<Book> books = bookRepository.searchBooks(
            request.getTitle(), 
            request.getAuthor(), 
            request.getCategory()
        );
        
        // 2. 過濾有可用副本的書籍
        return books.stream()
            .filter(book -> hasAvailableCopies(book))
            .collect(Collectors.toList());
    }
    
    public BookAvailabilityResponse checkBookAvailability(Long bookId, Long libraryId) {
        // 1. 查找書籍
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BookNotFoundException("Book not found"));
        
        // 2. 檢查可用副本
        List<BookCopy> availableCopies = bookCopyRepository.findAvailableCopies(
            bookId, libraryId, "AVAILABLE"
        );
        
        // 3. 建立回應
        BookAvailabilityResponse response = new BookAvailabilityResponse();
        response.setBookId(bookId);
        response.setLibraryId(libraryId);
        response.setAvailable(availableCopies.size() > 0);
        response.setAvailableCount(availableCopies.size());
        response.setTotalCount(bookCopyRepository.countByBook(book));
        
        return response;
    }
    
    private void validateBookRequest(BookCreationRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ValidationException("Book title is required");
        }
        
        if (request.getAuthor() == null || request.getAuthor().trim().isEmpty()) {
            throw new ValidationException("Book author is required");
        }
        
        if (request.getCopyCount() == null || request.getCopyCount() <= 0) {
            throw new ValidationException("Copy count must be greater than 0");
        }
    }
    
    private void createBookCopies(Book book, Long libraryId, Integer copyCount) {
        for (int i = 1; i <= copyCount; i++) {
            BookCopy copy = new BookCopy();
            copy.setBook(book);
            copy.setLibrary(libraryId); // 簡化，實際應該查找Library實體
            copy.setCopyNumber(i);
            copy.setStatus("AVAILABLE");
            
            bookCopyRepository.save(copy);
        }
    }
    
    private boolean hasAvailableCopies(Book book) {
        return bookCopyRepository.countByBookAndStatus(book, "AVAILABLE") > 0;
    }
}
```

### **3. 複雜業務邏輯**

#### **BorrowService範例**
```java
@Service
@Transactional
public class BorrowService {
    
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    public BorrowService(BorrowRecordRepository borrowRecordRepository,
                        BookCopyRepository bookCopyRepository,
                        UserRepository userRepository,
                        NotificationService notificationService) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }
    
    public BorrowRecord borrowBook(Long userId, Long bookCopyId) {
        // 1. 查找使用者
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // 2. 查找書籍副本
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
            .orElseThrow(() -> new BookCopyNotFoundException("Book copy not found"));
        
        // 3. 檢查借閱資格
        validateBorrowEligibility(user, bookCopy);
        
        // 4. 建立借閱記錄
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setUser(user);
        borrowRecord.setBookCopy(bookCopy);
        borrowRecord.setDueAt(calculateDueDate());
        borrowRecord.setStatus("BORROWED");
        
        // 5. 更新書籍副本狀態
        bookCopy.setStatus("BORROWED");
        bookCopyRepository.save(bookCopy);
        
        // 6. 儲存借閱記錄
        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
        
        // 7. 發送通知
        notificationService.sendBorrowConfirmation(user, savedRecord);
        
        return savedRecord;
    }
    
    public BorrowRecord returnBook(Long borrowRecordId) {
        // 1. 查找借閱記錄
        BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowRecordId)
            .orElseThrow(() -> new BorrowRecordNotFoundException("Borrow record not found"));
        
        // 2. 檢查是否已歸還
        if ("RETURNED".equals(borrowRecord.getStatus())) {
            throw new BookAlreadyReturnedException("Book already returned");
        }
        
        // 3. 更新借閱記錄
        borrowRecord.setReturnedAt(LocalDateTime.now());
        borrowRecord.setStatus("RETURNED");
        
        // 4. 更新書籍副本狀態
        BookCopy bookCopy = borrowRecord.getBookCopy();
        bookCopy.setStatus("AVAILABLE");
        bookCopyRepository.save(bookCopy);
        
        // 5. 儲存借閱記錄
        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
        
        // 6. 檢查是否逾期
        if (borrowRecord.getReturnedAt().isAfter(borrowRecord.getDueAt())) {
            notificationService.sendOverdueNotification(borrowRecord.getUser(), savedRecord);
        }
        
        return savedRecord;
    }
    
    public List<BorrowRecord> getOverdueRecords(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        return borrowRecordRepository.findOverdueRecords(
            userId, "BORROWED", LocalDateTime.now()
        );
    }
    
    private void validateBorrowEligibility(User user, BookCopy bookCopy) {
        // 1. 檢查書籍是否可用
        if (!"AVAILABLE".equals(bookCopy.getStatus())) {
            throw new BookNotAvailableException("Book is not available");
        }
        
        // 2. 檢查使用者借閱數量限制
        long activeBorrows = borrowRecordRepository.countByUserAndStatus(user, "BORROWED");
        if (activeBorrows >= 5) { // 假設最多借5本書
            throw new BorrowLimitExceededException("Borrow limit exceeded");
        }
        
        // 3. 檢查是否有逾期書籍
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords(
            user.getId(), "BORROWED", LocalDateTime.now()
        );
        if (!overdueRecords.isEmpty()) {
            throw new OverdueBooksException("User has overdue books");
        }
    }
    
    private LocalDateTime calculateDueDate() {
        // 借閱期限為30天
        return LocalDateTime.now().plusDays(30);
    }
}
```

---

## 🏗️ **建立Service類別**

### **步驟1: 建立service目錄**
```bash
mkdir -p src/main/java/com/library/service
```

### **步驟2: 建立UserService**
```java
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    
    public User registerUser(String username, String email, String password, String fullName) {
        // 驗證邏輯
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        // 取得預設角色
        Role userRole = roleRepository.findByName("USER")
            .orElseThrow(() -> new RuntimeException("Default role not found"));
        
        // 建立使用者
        User user = new User(username, password, email, fullName, userRole);
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }
}
```

### **步驟3: 建立BookService**
```java
@Service
@Transactional
public class BookService {
    
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    
    public BookService(BookRepository bookRepository, BookCopyRepository bookCopyRepository) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
    }
    
    public Book createBook(String title, String author, Integer publishedYear, String category) {
        Book book = new Book(title, author, publishedYear, category);
        return bookRepository.save(book);
    }
    
    public List<Book> searchBooks(String title, String author, String category) {
        return bookRepository.searchBooks(title, author, category);
    }
    
    public List<Book> findByCategory(String category) {
        return bookRepository.findByCategory(category);
    }
    
    public long countByCategory(String category) {
        return bookRepository.countByCategory(category);
    }
}
```

### **步驟4: 建立BorrowService**
```java
@Service
@Transactional
public class BorrowService {
    
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;
    
    public BorrowService(BorrowRecordRepository borrowRecordRepository,
                        BookCopyRepository bookCopyRepository,
                        UserRepository userRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.userRepository = userRepository;
    }
    
    public BorrowRecord borrowBook(Long userId, Long bookCopyId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
            .orElseThrow(() -> new RuntimeException("Book copy not found"));
        
        // 檢查是否可用
        if (!"AVAILABLE".equals(bookCopy.getStatus())) {
            throw new RuntimeException("Book is not available");
        }
        
        // 建立借閱記錄
        BorrowRecord borrowRecord = new BorrowRecord(user, bookCopy, LocalDateTime.now().plusDays(30));
        
        // 更新書籍狀態
        bookCopy.setStatus("BORROWED");
        bookCopyRepository.save(bookCopy);
        
        return borrowRecordRepository.save(borrowRecord);
    }
    
    public List<BorrowRecord> getOverdueRecords(Long userId) {
        return borrowRecordRepository.findOverdueRecords(userId, "BORROWED", LocalDateTime.now());
    }
    
    public List<BorrowRecord> findByUser(User user) {
        return borrowRecordRepository.findByUser(user);
    }
}
```

---

## 🧪 **測試Service**

### **建立測試類別**
```java
@SpringBootTest
@Transactional
class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testRegisterUser() {
        // Given
        String username = "testuser";
        String email = "test@example.com";
        String password = "password";
        String fullName = "Test User";
        
        // When
        User user = userService.registerUser(username, email, password, fullName);
        
        // Then
        assertNotNull(user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(fullName, user.getFullName());
    }
    
    @Test
    void testRegisterUserWithDuplicateUsername() {
        // Given
        String username = "testuser";
        userService.registerUser(username, "test1@example.com", "password", "Test User 1");
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.registerUser(username, "test2@example.com", "password", "Test User 2");
        });
    }
}
```

---

## ⚠️ **注意事項**

### **1. 事務管理**
- 使用 `@Transactional` 確保資料一致性
- 在Service層管理事務，而不是在Repository層
- 了解事務傳播行為，選擇合適的傳播類型
- 避免在事務方法中調用外部API

### **2. 異常處理**
- 定義自定義異常類別
- 在Service層拋出業務異常
- 在Controller層處理異常
- 使用 `@Transactional(rollbackFor = Exception.class)` 指定回滾異常

### **3. 驗證邏輯**
- 在Service層進行業務驗證
- 使用Bean Validation進行基本驗證
- 實作複雜的業務規則驗證
- 驗證失敗時拋出明確的異常

### **4. 依賴注入**
- 使用建構函式注入（推薦）
- 避免使用 `@Autowired` 欄位注入
- 確保依賴關係清晰
- 只注入真正需要的依賴

### **5. 測試策略**
- 為每個Service方法編寫測試
- 測試正常和異常情況
- 使用Mock物件測試依賴
- 測試事務回滾功能

### **6. 效能考量**
- 避免在事務中進行耗時操作
- 合理使用事務隔離級別
- 避免長事務，影響資料庫效能
- 考慮使用 `@Transactional(readOnly = true)` 優化查詢

---

## 📚 **下一步**

完成Service層建立後，下一步將是：
- **階段6: Controller層** - 建立API層
- 學習RESTful API設計
- 整合Service層和API層

---

## 🎯 **學習檢查清單**

- [ ] 了解Service層的作用和設計原則
- [ ] 學會建立Service類別
- [ ] 實作業務邏輯和驗證
- [ ] 處理異常和錯誤
- [ ] 整合Repository層
- [ ] 測試Service功能
- [ ] 了解事務管理
- [ ] 掌握依賴注入

---

**準備好開始建立Service層了嗎？我們將實作複雜的業務邏輯！**
