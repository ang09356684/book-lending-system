# 階段11: Swagger API 文檔建立

## 📋 **學習目標**

本階段將學習如何使用 Springdoc OpenAPI (Swagger) 建立完整的 API 文檔，讓前端開發者和其他團隊成員能夠輕鬆理解和使用我們的 API。

### **學習重點**
- 了解 Swagger/OpenAPI 的作用和優勢
- 學習 Springdoc OpenAPI 的配置
- 掌握 API 文檔註解的使用方法
- 建立完整的 API 文檔系統

---

## 🎯 **Swagger/OpenAPI 簡介**

### **什麼是 Swagger？**
Swagger 是一個用於設計、建立、文檔化和使用 RESTful API 的工具集。它提供了一個互動式的 API 文檔界面，讓開發者可以直接在瀏覽器中測試 API。

### **為什麼使用 Swagger？**
- ✅ **互動式文檔**: 可以直接在瀏覽器中測試 API
- ✅ **自動生成**: 根據程式碼註解自動生成文檔
- ✅ **即時更新**: 程式碼變更時文檔自動更新
- ✅ **團隊協作**: 前端和後端團隊可以更好地協作
- ✅ **API 測試**: 提供內建的 API 測試功能

### **Springdoc OpenAPI vs SpringFox**
- **Springdoc OpenAPI**: 支援 OpenAPI 3.x，Spring Boot 3.x 推薦
- **SpringFox**: 支援 Swagger 2.x，較舊版本

---

## 🔧 **環境配置**

### **步驟1: 添加依賴**

在 `pom.xml` 中添加 Springdoc OpenAPI 依賴：

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

### **步驟2: 配置 application.yml**

```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
    doc-expansion: none
    disable-swagger-default-url: true
  packages-to-scan: com.library.controller
```

### **步驟3: 建立 OpenAPI 配置類別**

```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Library Management System API")
                .version("1.0.0")
                .description("線上圖書借閱系統 API 文檔")
                .contact(new Contact()
                    .name("Library Team")
                    .email("library@example.com")
                    .url("https://github.com/library-system"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(Arrays.asList(
                new Server().url("http://localhost:8080").description("Development Server"),
                new Server().url("https://api.library.com").description("Production Server")
            ))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("請在登入後獲取 JWT Token，並在請求頭中加入 Authorization: Bearer {token}")
                )
            );
    }
}
```

---

## 📝 **API 文檔註解**

### **1. Controller 級別註解**

#### **@Tag**
用於分組 API 端點：

```java
@Tag(name = "Authentication", description = "使用者認證相關 API")
@RestController
@RequestMapping("/auth")
public class AuthController {
    // Controller methods
}
```

#### **@SecurityRequirement**
標示需要認證的端點：

```java
@SecurityRequirement(name = "Bearer Authentication")
@GetMapping("/profile")
public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
    // Implementation
}
```

### **2. 方法級別註解**

#### **@Operation**
描述 API 端點的功能：

```java
@Operation(
    summary = "使用者註冊",
    description = "註冊新的使用者帳號，支援一般用戶和館員註冊"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "註冊成功",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UserResponse.class),
            examples = @ExampleObject(
                value = """
                {
                    "success": true,
                    "message": "User registered successfully",
                    "data": {
                        "id": 1,
                        "name": "John Doe",
                        "email": "john@example.com",
                        "roleName": "MEMBER"
                    }
                }
                """
            )
        )
    ),
    @ApiResponse(
        responseCode = "400",
        description = "註冊失敗",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = """
                {
                    "success": false,
                    "message": "Email already exists",
                    "error": "Business error"
                }
                """
            )
        )
    )
})
@PostMapping("/register")
public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody @Valid RegisterRequest request) {
    // Implementation
}
```

#### **@Parameter**
描述請求參數：

```java
@Operation(summary = "搜尋書籍")
@GetMapping("/search")
public ResponseEntity<ApiResponse<List<BookWithCopySummaryResponse>>> searchBooks(
    @Parameter(
        description = "書籍標題關鍵字",
        example = "Java Programming",
        required = false
    )
    @RequestParam(required = false) String title,
    
    @Parameter(
        description = "作者姓名",
        example = "John Smith",
        required = false
    )
    @RequestParam(required = false) String author,
    
    @Parameter(
        description = "書籍分類",
        example = "Programming",
        required = false
    )
    @RequestParam(required = false) String category,
    
    @Parameter(
        description = "頁碼 (從 0 開始)",
        example = "0",
        required = false
    )
    @RequestParam(defaultValue = "0") int page,
    
    @Parameter(
        description = "每頁數量",
        example = "10",
        required = false
    )
    @RequestParam(defaultValue = "10") int size
) {
    // Implementation
}
```

### **3. DTO 級別註解**

#### **@Schema**
描述 DTO 欄位：

```java
@Schema(description = "使用者註冊請求")
public class RegisterRequest {
    
    @Schema(
        description = "使用者姓名",
        example = "John Doe",
        minLength = 2,
        maxLength = 100
    )
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @Schema(
        description = "電子郵件地址",
        example = "john@example.com",
        format = "email"
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;
    
    @Schema(
        description = "密碼",
        example = "password123",
        minLength = 6
    )
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
```

#### **@Schema 進階用法**

```java
@Schema(description = "館員註冊請求")
public class LibrarianRegisterRequest extends RegisterRequest {
    
    @Schema(
        description = "館員識別碼",
        example = "L001",
        pattern = "^L\\d{3,}$",
        minLength = 4
    )
    @NotBlank(message = "Librarian ID is required")
    @Pattern(regexp = "^L\\d{3,}$", message = "Librarian ID must start with 'L' followed by at least 3 digits")
    private String librarianId;
}
```

---

## 🏗️ **實際應用範例**

### **完整的 Controller 範例**

```java
@Tag(name = "Books", description = "書籍管理相關 API")
@RestController
@RequestMapping("/books")
@Validated
public class BookController {
    
    private final BookService bookService;
    private final UserService userService;
    
    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }
    
    @Operation(
        summary = "取得所有書籍",
        description = "分頁取得所有書籍列表，包含副本統計資訊"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "成功取得書籍列表",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BookWithCopySummaryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "未授權訪問"
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookWithCopySummaryResponse>>> getAllBooks(
        @Parameter(description = "頁碼 (從 0 開始)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        
        @Parameter(description = "每頁數量", example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        List<BookWithCopySummaryResponse> books = bookService.getAllBooksWithCopySummary(page, size);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @Operation(
        summary = "搜尋書籍",
        description = "根據標題、作者、分類搜尋書籍，支援模糊搜尋"
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BookWithCopySummaryResponse>>> searchBooks(
        @Parameter(description = "書籍標題關鍵字", example = "Java")
        @RequestParam(required = false) String title,
        
        @Parameter(description = "作者姓名", example = "John Smith")
        @RequestParam(required = false) String author,
        
        @Parameter(description = "書籍分類", example = "Programming")
        @RequestParam(required = false) String category,
        
        @Parameter(description = "頁碼", example = "0")
        @RequestParam(defaultValue = "0") int page,
        
        @Parameter(description = "每頁數量", example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        List<BookWithCopySummaryResponse> books = bookService.searchBooksWithCopySummary(
            title, author, category, page, size
        );
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @Operation(
        summary = "取得特定書籍",
        description = "根據書籍 ID 取得詳細資訊"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "成功取得書籍資訊"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "書籍不存在"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(
        @Parameter(description = "書籍 ID", example = "1")
        @PathVariable Long id
    ) {
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(ApiResponse.success(book));
    }
    
    @Operation(
        summary = "新增書籍",
        description = "館員專用：新增書籍到系統"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "書籍新增成功"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "權限不足，僅館員可操作"
        )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> createBook(
        @Parameter(description = "書籍資訊", required = true)
        @RequestBody @Valid CreateBookRequest request
    ) {
        checkLibrarianPermission();
        BookResponse book = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(book, "Book created successfully"));
    }
    
    @Operation(
        summary = "新增書籍副本",
        description = "館員專用：為現有書籍新增副本到指定圖書館"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/add-copies")
    public ResponseEntity<ApiResponse<String>> addBookCopies(
        @Parameter(description = "新增副本請求", required = true)
        @RequestBody @Valid AddBookCopiesRequest request
    ) {
        checkLibrarianPermission();
        bookService.addBookCopies(request);
        return ResponseEntity.ok(ApiResponse.success("Book copies added successfully"));
    }
    
    @Operation(
        summary = "取得書籍副本",
        description = "取得指定書籍的所有副本資訊"
    )
    @GetMapping("/{id}/copies")
    public ResponseEntity<ApiResponse<List<BookCopyResponse>>> getBookCopies(
        @Parameter(description = "書籍 ID", example = "1")
        @PathVariable Long id
    ) {
        List<BookCopyResponse> copies = bookService.getBookCopies(id);
        return ResponseEntity.ok(ApiResponse.success(copies));
    }
    
    @Operation(
        summary = "取得可用副本",
        description = "取得指定書籍的可用副本資訊"
    )
    @GetMapping("/{id}/copies/available")
    public ResponseEntity<ApiResponse<List<BookCopyResponse>>> getAvailableCopies(
        @Parameter(description = "書籍 ID", example = "1")
        @PathVariable Long id
    ) {
        List<BookCopyResponse> copies = bookService.getAvailableBookCopies(id);
        return ResponseEntity.ok(ApiResponse.success(copies));
    }
    
    // Helper methods
    private void checkLibrarianPermission() {
        if (!userService.isLibrarian()) {
            throw new AccessDeniedException("Only librarians can perform this operation");
        }
    }
}
```

### **完整的 DTO 範例**

```java
@Schema(description = "書籍建立請求")
public class CreateBookRequest {
    
    @Schema(
        description = "書籍標題",
        example = "Java Programming Practice Guide",
        minLength = 1,
        maxLength = 200
    )
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @Schema(
        description = "作者姓名",
        example = "John Smith",
        minLength = 1,
        maxLength = 200
    )
    @NotBlank(message = "Author is required")
    @Size(max = 200, message = "Author must not exceed 200 characters")
    private String author;
    
    @Schema(
        description = "出版年份",
        example = "2023",
        minimum = "1900",
        maximum = "2025"
    )
    @NotNull(message = "Published year is required")
    @Min(value = 1900, message = "Published year must be at least 1900")
    @Max(value = 2025, message = "Published year must not exceed 2025")
    private Integer publishedYear;
    
    @Schema(
        description = "書籍分類",
        example = "Programming",
        allowableValues = {"Programming", "Computer Science", "Fiction", "Non-Fiction"}
    )
    @NotBlank(message = "Category is required")
    private String category;
    
    @Schema(
        description = "書籍類型",
        example = "圖書",
        allowableValues = {"圖書", "書籍"}
    )
    @NotBlank(message = "Book type is required")
    private String bookType;
}

@Schema(description = "書籍副本新增請求")
public class AddBookCopiesRequest {
    
    @Schema(
        description = "書籍 ID",
        example = "1"
    )
    @NotNull(message = "Book ID is required")
    private Long bookId;
    
    @Schema(
        description = "副本配置列表",
        required = true
    )
    @Valid
    @NotEmpty(message = "At least one library copy configuration is required")
    private List<LibraryCopyConfig> libraryCopies;
    
    @Schema(description = "圖書館副本配置")
    public static class LibraryCopyConfig {
        
        @Schema(
            description = "圖書館 ID",
            example = "1"
        )
        @NotNull(message = "Library ID is required")
        private Long libraryId;
        
        @Schema(
            description = "副本數量",
            example = "3",
            minimum = "1"
        )
        @NotNull(message = "Number of copies is required")
        @Min(value = 1, message = "Number of copies must be at least 1")
        private Integer numberOfCopies;
    }
}
```

---

## 🎨 **自訂 Swagger UI**

### **自訂主題**

在 `application.yml` 中添加自訂配置：

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
    doc-expansion: none
    disable-swagger-default-url: true
    # 自訂主題
    theme: "material"
    # 自訂 CSS
    custom-css-url: "/css/swagger-custom.css"
    # 自訂 JavaScript
    custom-js-url: "/js/swagger-custom.js"
```

### **自訂 CSS 範例**

```css
/* /src/main/resources/static/css/swagger-custom.css */
.swagger-ui .topbar {
    background-color: #2c3e50;
}

.swagger-ui .info .title {
    color: #2c3e50;
}

.swagger-ui .opblock.opblock-get .opblock-summary-method {
    background-color: #61affe;
}

.swagger-ui .opblock.opblock-post .opblock-summary-method {
    background-color: #49cc90;
}

.swagger-ui .opblock.opblock-put .opblock-summary-method {
    background-color: #fca130;
}

.swagger-ui .opblock.opblock-delete .opblock-summary-method {
    background-color: #f93e3e;
}
```

---

## 🔒 **安全性配置**

### **生產環境配置**

```yaml
# application-prod.yml
springdoc:
  api-docs:
    enabled: false  # 停用 API 文檔
  swagger-ui:
    enabled: false  # 停用 Swagger UI
```

### **條件性啟用**

```java
@Configuration
@ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "true", matchIfMissing = true)
public class OpenApiConfig {
    // OpenAPI 配置
}
```

---

## 📊 **最佳實踐**

### **1. 文檔組織**
- **按功能分組**: 使用 `@Tag` 將相關 API 分組
- **清晰的描述**: 提供簡潔但完整的 API 描述
- **一致的命名**: 保持 API 端點命名的一致性

### **2. 參數說明**
- **詳細描述**: 為每個參數提供清楚的描述
- **範例值**: 提供實際可用的範例值
- **驗證規則**: 說明參數的驗證規則和限制

### **3. 回應範例**
- **成功案例**: 提供成功回應的完整範例
- **錯誤案例**: 包含常見錯誤的回應範例
- **真實資料**: 使用接近真實情況的範例資料

### **4. 安全性**
- **認證說明**: 清楚說明 API 的認證要求
- **權限說明**: 說明不同角色的權限差異
- **安全標示**: 為需要認證的端點加上安全標示

### **5. 版本管理**
- **版本標示**: 清楚標示 API 版本
- **變更記錄**: 記錄 API 的變更歷史
- **向下相容**: 盡量保持向下相容性

---

## 🧪 **測試 API 文檔**

### **啟動應用程式**

```bash
mvn spring-boot:run
```

### **訪問 Swagger UI**

打開瀏覽器訪問：`http://localhost:8080/swagger-ui.html`

### **測試 API**

1. **註冊新用戶**
   - 找到 `/auth/register` 端點
   - 點擊 "Try it out"
   - 輸入測試資料
   - 點擊 "Execute"

2. **登入獲取 Token**
   - 找到 `/auth/login` 端點
   - 輸入註冊的 email 和密碼
   - 複製回應中的 token

3. **測試需要認證的 API**
   - 點擊右上角的 "Authorize" 按鈕
   - 輸入 `Bearer {your-token}`
   - 測試需要認證的端點

---

## 📚 **相關資源**

- [Springdoc OpenAPI 官方文件](https://springdoc.org/)
- [OpenAPI 3.0 規格](https://swagger.io/specification/)
- [Swagger UI 自訂指南](https://swagger.io/docs/open-source-tools/swagger-ui/customization/)

---

**記住：好的 API 文檔是團隊協作的基礎！** 🎯
