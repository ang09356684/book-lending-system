# Springdoc OpenAPI 套件說明

## 目錄
1. [什麼是 Springdoc OpenAPI](#什麼是-springdoc-openapi)
2. [為什麼需要 API 文件](#為什麼需要-api-文件)
3. [核心概念](#核心概念)
4. [配置方式](#配置方式)
5. [常用註解](#常用註解)
6. [實際應用範例](#實際應用範例)
7. [最佳實踐](#最佳實踐)
8. [常見問題](#常見問題)

---

## 什麼是 Springdoc OpenAPI

### 定義
Springdoc OpenAPI 是一個 Spring Boot 整合套件，用於自動生成 OpenAPI 3.0 規格的 API 文件，並提供 Swagger UI 介面。

### 主要功能
- **自動生成 API 文件**：根據程式碼註解自動產生 OpenAPI 規格
- **互動式測試介面**：提供 Swagger UI 讓開發者可以直接測試 API
- **多種格式支援**：支援 JSON、YAML 等格式的 API 文件輸出
- **安全性整合**：支援 JWT、OAuth2 等認證方式的文件化

### 與其他工具的比較
| 工具 | 優點 | 缺點 |
|------|------|------|
| Springdoc OpenAPI | 自動生成、整合性好、功能完整 | 學習曲線較陡 |
| Swagger UI | 介面美觀、易於使用 | 需要手動維護 |
| Postman | 功能強大、支援測試 | 不是文件生成工具 |

---

## 為什麼需要 API 文件

### 開發階段
- **團隊協作**：讓前端和後端開發者清楚了解 API 規格
- **快速測試**：提供互動式介面進行 API 測試
- **減少溝通成本**：避免重複詢問 API 細節

### 維護階段
- **新成員上手**：新加入的開發者可以快速了解系統
- **版本管理**：清楚記錄 API 的變更歷史
- **問題排查**：當 API 出現問題時，文件提供重要參考

### 部署階段
- **客戶支援**：提供給客戶或第三方開發者使用
- **API 管理**：作為 API 管理平台的重要組成部分

---

## 核心概念

### OpenAPI 規格
OpenAPI 是一個標準化的 API 文件規格，定義了：
- **API 端點**：URL 路徑和 HTTP 方法
- **請求參數**：路徑參數、查詢參數、請求體
- **回應格式**：狀態碼、回應內容結構
- **認證方式**：API Key、Bearer Token、OAuth2 等

### Swagger UI
Swagger UI 是一個基於 OpenAPI 規格的互動式 API 文件介面，提供：
- **API 瀏覽**：以視覺化方式展示所有 API 端點
- **互動測試**：直接在瀏覽器中測試 API
- **參數說明**：詳細的參數類型和範例
- **回應範例**：各種狀態碼的回應範例

### 自動生成機制
Springdoc OpenAPI 透過以下方式自動生成文件：
1. **掃描註解**：讀取 `@RestController`、`@RequestMapping` 等註解
2. **分析參數**：解析 `@RequestParam`、`@PathVariable`、`@RequestBody` 等
3. **推斷類型**：根據 Java 類型推斷 OpenAPI 資料類型
4. **生成文件**：產生符合 OpenAPI 3.0 規格的 JSON/YAML 文件

---

## 配置方式

### 1. 加入依賴

```xml
<!-- Maven -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

### 2. 基本配置

```yaml
# application.yml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
  packages-to-scan: com.library.controller
```

### 3. 進階配置類別

```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(servers())
                .components(components())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }
    
    private Info apiInfo() {
        return new Info()
                .title("Library Management System API")
                .description("線上圖書借閱系統 RESTful API 文件")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Library System Team")
                        .email("support@library.com"));
    }
    
    private List<Server> servers() {
        return List.of(
                new Server().url("http://localhost:8080").description("Development Server"),
                new Server().url("https://api.library.com").description("Production Server")
        );
    }
    
    private Components components() {
        return new Components()
                .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme());
    }
    
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
}
```

---

## 常用註解

### 類別層級註解

#### @Tag
用於為 Controller 分類和描述：

```java
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    // ...
}
```

### 方法層級註解

#### @Operation
描述 API 端點的基本資訊：

```java
@Operation(
    summary = "User login",
    description = "Authenticate user and return JWT token"
)
@PostMapping("/login")
public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
    // ...
}
```

#### @ApiResponses
定義各種回應狀態：

```java
@ApiResponses(value = {
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Login successful",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = """
                {
                    "success": true,
                    "message": "Login successful",
                    "data": {
                        "token": "eyJhbGciOiJIUzUxMiJ9...",
                        "user": {
                            "id": 1,
                            "username": "john_doe"
                        }
                    }
                }
                """
            )
        )
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Invalid credentials"
    )
})
```

### 參數層級註解

#### @Parameter
描述方法參數：

```java
public ResponseEntity<ApiResponse<Book>> getBook(
    @Parameter(description = "Book ID", required = true, example = "1")
    @PathVariable Long id
) {
    // ...
}
```

#### @Schema
描述複雜物件結構：

```java
@Schema(description = "User registration request")
public class RegisterRequest {
    
    @Schema(description = "User name", example = "John Doe", minLength = 2)
    @NotBlank(message = "Name is required")
    private String name;
    
    @Schema(description = "User email address", example = "john@example.com")
    @Email(message = "Email format is invalid")
    private String email;
}
```

### 安全性註解

#### @SecurityRequirement
標示需要認證的端點：

```java
@SecurityRequirement(name = "Bearer Authentication")
@GetMapping("/profile")
public ResponseEntity<ApiResponse<User>> getProfile() {
    // ...
}
```

---

## 實際應用範例

### 完整的 Controller 範例

```java
@Tag(name = "Books", description = "Book management endpoints")
@RestController
@RequestMapping("/api/books")
public class BookController {
    
    @Operation(
        summary = "Create new book",
        description = "Create a new book with the provided information"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Book created successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "message": "Book created successfully",
                        "data": {
                            "id": 1,
                            "title": "The Great Gatsby",
                            "author": "F. Scott Fitzgerald",
                            "publishedYear": 1925,
                            "category": "Fiction"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid input data"
        )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Book>> createBook(
        @Parameter(description = "Book creation information", required = true)
        @RequestBody @Valid CreateBookRequest request
    ) {
        // Implementation
    }
    
    @Operation(
        summary = "Search books",
        description = "Search books by title, author, or category"
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Book>>> searchBooks(
        @Parameter(description = "Book title to search for", example = "Gatsby")
        @RequestParam(required = false) String title,
        @Parameter(description = "Book author to search for", example = "Fitzgerald")
        @RequestParam(required = false) String author,
        @Parameter(description = "Book category to search for", example = "Fiction")
        @RequestParam(required = false) String category
    ) {
        // Implementation
    }
}
```

### DTO 範例

```java
@Schema(description = "User registration request")
public class RegisterRequest {
    
    @Schema(
        description = "User name",
        example = "John Doe",
        minLength = 2,
        maxLength = 100
    )
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @Schema(
        description = "User email address",
        example = "john@example.com",
        format = "email"
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;
    
    @Schema(
        description = "User password",
        example = "password123",
        minLength = 6
    )
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
```

---

## 最佳實踐

### 1. 文件組織
- **按功能分組**：使用 `@Tag` 將相關的 API 端點分組
- **清晰的描述**：提供簡潔但完整的 API 描述
- **一致的命名**：保持 API 端點命名的一致性

### 2. 參數說明
- **詳細描述**：為每個參數提供清楚的描述
- **範例值**：提供實際可用的範例值
- **驗證規則**：說明參數的驗證規則和限制

### 3. 回應範例
- **成功案例**：提供成功回應的完整範例
- **錯誤案例**：包含常見錯誤的回應範例
- **真實資料**：使用接近真實情況的範例資料

### 4. 安全性
- **認證說明**：清楚說明 API 的認證要求
- **權限說明**：說明不同角色的權限差異
- **安全標示**：為需要認證的端點加上安全標示

### 5. 版本管理
- **版本標示**：清楚標示 API 版本
- **變更記錄**：記錄 API 的變更歷史
- **向下相容**：盡量保持向下相容性

### 6. 效能考量
- **分頁支援**：為大量資料的 API 提供分頁說明
- **快取策略**：說明 API 的快取策略
- **限制說明**：說明 API 的呼叫限制

---

## 常見問題

### Q1: 如何處理 import 衝突？
當 `ApiResponse` 類別名稱衝突時，使用完整類別名稱：

```java
// 避免 import 衝突
@io.swagger.v3.oas.annotations.responses.ApiResponse(
    responseCode = "200",
    description = "Success"
)
```

### Q2: 如何自訂 Swagger UI 樣式？
在 `application.yml` 中配置：

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
    doc-expansion: none
    disable-swagger-default-url: true
```

### Q3: 如何隱藏某些端點？
使用 `@Hidden` 註解：

```java
@Hidden
@GetMapping("/internal/health")
public ResponseEntity<String> health() {
    return ResponseEntity.ok("OK");
}
```

### Q4: 如何處理複雜的請求/回應結構？
使用 `@Schema` 註解詳細描述：

```java
@Schema(description = "Complex response structure")
public class ComplexResponse {
    
    @Schema(description = "Main data", implementation = User.class)
    private User user;
    
    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;
    
    @Schema(description = "Related items", type = "array")
    private List<RelatedItem> relatedItems;
}
```

### Q5: 如何測試 API 文件？
1. **啟動應用程式**：運行 Spring Boot 應用程式
2. **訪問 Swagger UI**：開啟 `http://localhost:8080/swagger-ui.html`
3. **瀏覽 API**：查看所有可用的 API 端點
4. **測試端點**：使用 Swagger UI 的 "Try it out" 功能測試 API
5. **檢查文件**：查看 `http://localhost:8080/v3/api-docs` 的 JSON 格式文件

---

## 總結

Springdoc OpenAPI 是一個強大的 API 文件生成工具，它能夠：

1. **自動生成**：根據程式碼註解自動產生完整的 API 文件
2. **互動測試**：提供 Swagger UI 介面進行 API 測試
3. **標準化**：遵循 OpenAPI 3.0 標準，確保文件的一致性
4. **易於維護**：文件與程式碼同步，減少維護成本

透過正確使用各種註解和配置，我們可以建立專業級的 API 文件，提升開發效率和團隊協作品質。

---

## 參考資源

- [Springdoc OpenAPI 官方文件](https://springdoc.org/)
- [OpenAPI 3.0 規格](https://swagger.io/specification/)
- [Swagger UI 文件](https://swagger.io/tools/swagger-ui/)
- [Spring Boot 整合指南](https://spring.io/guides/gs/spring-boot/)
