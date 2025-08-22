# 階段7: Controller層建立

## 📋 **學習目標**

本文件將詳細說明如何建立Spring Boot的Controller層，實現RESTful API端點。

### **學習重點**
- 了解Controller層的作用和設計原則
- 學習RESTful API的設計規範
- 掌握Spring Boot Controller的註解使用
- 理解請求和回應的處理機制
- 學會錯誤處理和驗證

---

## 🎯 **Controller層概述**

### **什麼是Controller層？**

Controller層是Spring Boot應用程式的最上層，負責：
- 接收HTTP請求
- 處理請求參數
- 呼叫Service層處理業務邏輯
- 返回HTTP回應

### **Controller層的職責**

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserService userService;
    
    // 接收HTTP請求
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // 1. 驗證請求參數
        // 2. 呼叫Service層
        // 3. 處理回應
        // 4. 返回HTTP狀態
    }
}
```

---

## 🔧 **RESTful API設計**

### **1. HTTP方法對應**

| HTTP方法 | 用途 | 範例 |
|----------|------|------|
| **GET** | 查詢資料 | `GET /api/books` |
| **POST** | 建立資料 | `POST /api/books` |
| **PUT** | 更新資料 | `PUT /api/books/{id}` |
| **DELETE** | 刪除資料 | `DELETE /api/books/{id}` |

### **2. URL設計規範**

```java
// ✅ 好的設計
GET    /api/books              // 查詢所有書籍
GET    /api/books/{id}         // 查詢特定書籍
POST   /api/books              // 新增書籍
PUT    /api/books/{id}         // 更新書籍
DELETE /api/books/{id}         // 刪除書籍

GET    /api/users/{id}/books   // 查詢使用者的借閱記錄
POST   /api/books/{id}/borrow  // 借閱書籍
POST   /api/books/{id}/return  // 歸還書籍

// ❌ 不好的設計
GET    /api/getBooks
POST   /api/createBook
GET    /api/getUserBooks
```

### **3. 回應格式統一**

```java
// 成功回應
{
    "success": true,
    "data": {
        "id": 1,
        "title": "Java Programming",
        "author": "John Doe"
    },
    "message": "Book created successfully"
}

// 錯誤回應
{
    "success": false,
    "error": "Book not found",
    "message": "The requested book does not exist"
}
```

---

## 📝 **Controller實作**

### **1. 基本Controller結構**

```java
@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {
    
    private final BookService bookService;
    
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    // API端點實作...
}
```

### **2. 常用註解說明**

#### **@RestController**
```java
@RestController  // 等同於 @Controller + @ResponseBody
public class BookController {
    // 所有方法都會自動序列化為JSON
}
```
**作用**: 
- 標記這個類別為REST控制器
- 等同於 `@Controller` + `@ResponseBody`
- 所有方法的返回值都會自動序列化為JSON
- 不需要在每個方法上加 `@ResponseBody`

#### **@RequestMapping**
```java
@RequestMapping("/api/books")  // 基礎路徑
public class BookController {
    
    @GetMapping              // GET /api/books
    public List<Book> getAllBooks() { }
    
    @GetMapping("/{id}")     // GET /api/books/{id}
    public Book getBook(@PathVariable Long id) { }
    
    @PostMapping             // POST /api/books
    public Book createBook(@RequestBody Book book) { }
}
```
**作用**:
- 定義這個控制器的基礎URL路徑
- 所有這個控制器中的方法都會以指定的路徑為前綴
- 可以設定在類別層級或方法層級

#### **@Validated**
```java
@RestController
@RequestMapping("/api/books")
@Validated  // 啟用方法參數驗證
public class BookController {
    
    @PostMapping
    public ResponseEntity<ApiResponse<Book>> createBook(
        @Valid @RequestBody CreateBookRequest request  // 驗證請求體
    ) {
        // ...
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Book>>> searchBooks(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) @Size(max = 100) String author  // 驗證參數
    ) {
        // ...
    }
}
```
**作用**:
- 啟用方法參數驗證功能
- 來源: `org.springframework.validation.annotation.Validated`
- 允許在方法參數上使用驗證註解
- 可以設定在類別層級（影響所有方法）或方法層級（只影響特定方法）

#### **@PathVariable**
```java
@GetMapping("/{id}")
public Book getBook(@PathVariable Long id) {
    // id 會從 URL 路徑中提取
    // 例如：/api/books/123 -> id = 123
}
```

#### **@RequestParam**
```java
@GetMapping("/search")
public List<Book> searchBooks(
    @RequestParam(required = false) String title,
    @RequestParam(required = false) String author
) {
    // 從查詢參數中提取
    // 例如：/api/books/search?title=Java&author=John
}
```

#### **@RequestBody**
```java
@PostMapping
public Book createBook(@RequestBody @Valid BookRequest request) {
    // 從請求體中提取JSON資料
    // @Valid 會觸發驗證
}
```

#### **@Valid**
```java
@PostMapping
public ResponseEntity<ApiResponse<Book>> createBook(
    @Valid @RequestBody CreateBookRequest request  // 驗證整個請求物件
) {
    // 如果驗證失敗，會拋出 MethodArgumentNotValidException
}
```
**作用**:
- 觸發對請求物件的驗證
- 會檢查物件中所有帶有驗證註解的欄位
- 驗證失敗時會拋出 `MethodArgumentNotValidException`

---

## 🎯 **實作範例**

### **1. AuthController - 認證相關**

```java
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 使用者註冊
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody @Valid RegisterRequest request) {
        try {
            User user = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFullName()
            );
            
            return ResponseEntity.ok(ApiResponse.success(user, "User registered successfully"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 館員註冊
     */
    @PostMapping("/register/librarian")
    public ResponseEntity<ApiResponse<User>> registerLibrarian(@RequestBody @Valid LibrarianRegisterRequest request) {
        try {
            User librarian = userService.registerLibrarian(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFullName(),
                request.getLibrarianId()
            );
            
            return ResponseEntity.ok(ApiResponse.success(librarian, "Librarian registered successfully"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
```

### **2. BookController - 書籍管理**

```java
@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {
    
    private final BookService bookService;
    
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    /**
     * 查詢所有書籍
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Book>>> getAllBooks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        List<Book> books = bookService.getAllBooks(page, size);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    /**
     * 查詢特定書籍
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> getBook(@PathVariable Long id) {
        try {
            Book book = bookService.findById(id);
            return ResponseEntity.ok(ApiResponse.success(book));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 新增書籍
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Book>> createBook(@RequestBody @Valid CreateBookRequest request) {
        try {
            Book book = bookService.createBook(
                request.getTitle(),
                request.getAuthor(),
                request.getPublishedYear(),
                request.getCategory()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(book, "Book created successfully"));
                
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 搜尋書籍
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Book>>> searchBooks(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String author,
        @RequestParam(required = false) String category
    ) {
        List<Book> books = bookService.searchBooks(title, author, category);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
}
```

### **3. BorrowController - 借閱管理**

```java
@RestController
@RequestMapping("/api/borrows")
@Validated
public class BorrowController {
    
    private final BorrowService borrowService;
    
    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }
    
    /**
     * 借閱書籍
     */
    @PostMapping("/{bookId}/borrow")
    public ResponseEntity<ApiResponse<BorrowRecord>> borrowBook(
        @PathVariable Long bookId,
        @RequestBody @Valid BorrowRequest request
    ) {
        try {
            BorrowRecord record = borrowService.borrowBook(
                request.getUserId(),
                bookId,
                request.getLibraryId()
            );
            
            return ResponseEntity.ok(ApiResponse.success(record, "Book borrowed successfully"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 歸還書籍
     */
    @PostMapping("/{recordId}/return")
    public ResponseEntity<ApiResponse<BorrowRecord>> returnBook(@PathVariable Long recordId) {
        try {
            BorrowRecord record = borrowService.returnBook(recordId);
            return ResponseEntity.ok(ApiResponse.success(record, "Book returned successfully"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 查詢借閱記錄
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BorrowRecord>>> getUserBorrows(@PathVariable Long userId) {
        List<BorrowRecord> records = borrowService.findByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}
```

---

## 📊 **請求和回應物件**

### **1. 驗證註解詳解**

#### **常用驗證註解**

| 註解 | 功能 | 範例 |
|------|------|------|
| `@NotNull` | 不能為null | `@NotNull private String field;` |
| `@NotBlank` | 不能為空字串或空白 | `@NotBlank private String field;` |
| `@NotEmpty` | 不能為空集合 | `@NotEmpty private List<String> list;` |
| `@Size` | 大小限制 | `@Size(min=3, max=50) private String field;` |
| `@Min` | 最小值 | `@Min(0) private Integer number;` |
| `@Max` | 最大值 | `@Max(100) private Integer number;` |
| `@Email` | 電子郵件格式 | `@Email private String email;` |
| `@Pattern` | 正則表達式 | `@Pattern(regexp="^[A-Za-z]+$")` |

#### **驗證註解使用範例**
```java
public class CreateBookRequest {
    @NotBlank(message = "Book title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @NotBlank(message = "Book author is required")
    @Size(max = 100, message = "Author must not exceed 100 characters")
    private String author;
    
    @Email(message = "Invalid email format")
    private String contactEmail;
    
    @Min(value = 1900, message = "Published year must be after 1900")
    @Max(value = 2024, message = "Published year cannot be in the future")
    private Integer publishedYear;
    
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Category must contain only letters and spaces")
    private String category;
}
```

### **2. 請求物件 (Request DTOs)**

```java
// 註冊請求
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    // getters and setters
}

// 館員註冊請求
public class LibrarianRegisterRequest extends RegisterRequest {
    @NotBlank(message = "Librarian ID is required")
    private String librarianId;
    
    // getters and setters
}

// 借閱請求
public class BorrowRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Library ID is required")
    private Long libraryId;
    
    // getters and setters
}
```

### **2. 回應物件 (Response DTOs)**

```java
// 統一API回應格式
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private String error;
    
    // 成功回應
    public static <T> ApiResponse<T> success(T data) {
        return success(data, null);
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setMessage(message);
        return response;
    }
    
    // 錯誤回應
    public static <T> ApiResponse<T> error(String error) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setError(error);
        return response;
    }
    
    // getters and setters
}
```

---

## ⚠️ **錯誤處理**

### **1. 驗證錯誤處理機制**

當使用`@Valid`或`@Validated`時，如果驗證失敗，Spring會拋出`MethodArgumentNotValidException`：

#### **驗證錯誤回應範例**

當驗證失敗時，API會返回：
```json
{
    "success": false,
    "error": "Validation failed",
    "message": "Username is required, Email must be a valid email format, Password must be at least 6 characters"
}
```

#### **驗證錯誤處理流程**

1. **客戶端發送請求** → 包含無效資料
2. **Spring驗證** → 發現驗證錯誤
3. **拋出異常** → `MethodArgumentNotValidException`
4. **全域處理器捕獲** → `GlobalExceptionHandler`
5. **返回錯誤回應** → 統一的錯誤格式

### **2. 全域異常處理**

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 處理驗證錯誤
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());
        
        String errorMessage = String.join(", ", errors);
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(errorMessage));
    }
    
    /**
     * 處理業務邏輯錯誤
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(ex.getMessage()));
    }
    
    /**
     * 處理一般異常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("Internal server error"));
    }
}
```

### **2. 自定義異常**

```java
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long id) {
        super("Book not found with id: " + id);
    }
}

public class BookAlreadyBorrowedException extends RuntimeException {
    public BookAlreadyBorrowedException(Long bookId) {
        super("Book is already borrowed: " + bookId);
    }
}
```

---

## 🎯 **最佳實踐**

### **1. 設計原則**

- ✅ **單一職責** - 每個Controller只處理特定領域
- ✅ **RESTful設計** - 遵循REST規範
- ✅ **統一回應格式** - 所有API使用相同的回應結構
- ✅ **適當的HTTP狀態碼** - 使用正確的狀態碼

### **2. 程式碼品質**

- ✅ **輸入驗證** - 使用@Valid進行參數驗證
- ✅ **錯誤處理** - 統一的異常處理機制
- ✅ **日誌記錄** - 記錄重要的API呼叫
- ✅ **API文件** - 使用註解說明API用途

### **3. 安全性**

- ✅ **參數驗證** - 防止惡意輸入
- ✅ **權限控制** - 確保只有授權使用者能存取
- ✅ **輸入清理** - 防止SQL注入等攻擊

---

## 📊 **總結**

### **Controller層的核心要素**

1. **@RestController** - 標記為REST控制器
2. **@RequestMapping** - 定義API路徑
3. **HTTP方法註解** - @GetMapping, @PostMapping等
4. **參數註解** - @PathVariable, @RequestParam, @RequestBody
5. **驗證註解** - @Valid, @NotBlank等
6. **統一回應格式** - ApiResponse<T>
7. **異常處理** - GlobalExceptionHandler

### **驗證機制核心要素**

1. **@Validated** - 啟用方法參數驗證
2. **@Valid** - 觸發請求物件驗證
3. **驗證註解** - @NotBlank, @Size, @Email等
4. **錯誤處理** - MethodArgumentNotValidException處理
5. **統一回應** - 驗證錯誤的標準化回應

### **學習檢查清單**

- [ ] 了解Controller層的作用
- [ ] 掌握RESTful API設計規範
- [ ] 學會使用Spring Boot註解
- [ ] 理解請求和回應處理
- [ ] 掌握錯誤處理機制
- [ ] 能夠設計統一的API格式
- [ ] 學會編寫完整的Controller

---

**現在您已經了解了Controller層的核心概念和實作方法！**
