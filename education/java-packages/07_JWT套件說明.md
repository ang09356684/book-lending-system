# Java套件學習 - JWT (JSON Web Token)

## 📋 **什麼是 JWT？**

JWT (JSON Web Token) 是一種開放標準 (RFC 7519)，用於在各方之間安全地傳輸資訊。它是一種自包含的、輕量級的認證方式，特別適合分散式系統和微服務架構。

### **為什麼需要 JWT？**

在現代 Web 應用程式中，傳統的會話管理方式存在一些問題：
- **伺服器端儲存**: 需要伺服器儲存會話資訊
- **擴展性問題**: 在分散式系統中難以共享會話
- **跨域問題**: 不同域名間無法共享會話
- **無狀態**: 違反 RESTful 的無狀態原則

JWT 解決了這些問題：
- ✅ **無狀態**: 伺服器不需要儲存會話資訊
- ✅ **可擴展**: 適合分散式系統和微服務
- ✅ **自包含**: Token 包含所有必要資訊
- ✅ **跨域支援**: 可以在不同域名間使用

---

## 🏗️ **JWT 結構**

### **JWT 的三個部分**

JWT 由三個部分組成，用點 (.) 分隔：

```
Header.Payload.Signature
```

#### **1. Header (標頭)**
包含 Token 類型和使用的演算法：

```json
{
  "alg": "HS512",
  "typ": "JWT"
}
```

#### **2. Payload (載荷)**
包含聲明 (claims)，分為三種類型：

**Registered Claims (註冊聲明)**：
```json
{
  "iss": "library-system",     // 發行者
  "sub": "john.doe",          // 主題 (使用者)
  "aud": "library-users",     // 受眾
  "exp": 1640995200,          // 過期時間
  "nbf": 1640908800,          // 生效時間
  "iat": 1640908800           // 簽發時間
}
```

**Public Claims (公開聲明)**：
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER"
}
```

**Private Claims (私有聲明)**：
```json
{
  "user_id": 123,
  "permissions": ["READ_BOOKS", "BORROW_BOOKS"]
}
```

#### **3. Signature (簽名)**
用於驗證 Token 的完整性：

```
HMACSHA512(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret
)
```

---

## 🔧 **JWT 實作**

### **1. 添加依賴**

在 `pom.xml` 中添加 JWT 依賴：

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

### **2. JWT Token Provider**

```java
@Component
public class JwtTokenProvider {
    
    @Value("${app.jwt.secret:defaultSecretKeyForDevelopmentOnly}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration:86400000}") // 24 hours
    private long jwtExpirationMs;
    
    /**
     * Generate JWT token from authentication
     */
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername());
    }
    
    /**
     * Generate JWT token from username
     */
    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }
    
    /**
     * Get username from JWT token
     */
    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        
        return claims.getSubject();
    }
    
    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            
            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            return claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }
}
```

### **3. JWT 認證過濾器**

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extract JWT token from Authorization header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
```

---

## 🔐 **JWT 工作流程**

### **1. 登入流程**

```
1. 使用者提交登入請求
   POST /api/auth/login
   {
     "username": "john.doe",
     "password": "password123"
   }

2. 伺服器驗證憑證
   - 檢查使用者名稱和密碼
   - 載入使用者權限

3. 生成 JWT Token
   - 包含使用者資訊
   - 設定過期時間
   - 使用密鑰簽名

4. 返回 Token
   {
     "success": true,
     "data": {
       "token": "eyJhbGciOiJIUzUxMiJ9...",
       "tokenType": "Bearer",
       "user": { ... }
     }
   }
```

### **2. 請求認證流程**

```
1. 客戶端發送請求
   GET /api/books
   Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...

2. JWT 過濾器處理
   - 提取 Authorization header
   - 移除 "Bearer " 前綴
   - 驗證 Token 簽名
   - 檢查 Token 過期時間

3. 載入使用者資訊
   - 從 Token 中提取使用者名稱
   - 從資料庫載入使用者詳情
   - 設定 Spring Security 認證

4. 授權檢查
   - 檢查使用者權限
   - 決定是否允許存取

5. 返回回應
   - 如果授權成功，返回請求的資料
   - 如果授權失敗，返回 401 Unauthorized
```

---

## 🛡️ **JWT 安全考量**

### **1. 密鑰管理**

```java
// 使用強密鑰 (至少 256 位元)
@Value("${app.jwt.secret}")
private String jwtSecret;

// 在生產環境中使用環境變數
// app.jwt.secret=${JWT_SECRET}
```

### **2. Token 過期時間**

```java
// 設定合理的過期時間
@Value("${app.jwt.expiration:86400000}") // 24 hours
private long jwtExpirationMs;

// 可以設定不同的過期時間
@Value("${app.jwt.refresh-expiration:604800000}") // 7 days
private long refreshExpirationMs;
```

### **3. Token 刷新機制**

```java
@Component
public class JwtTokenProvider {
    
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationMs);
        
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .claim("type", "refresh")
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }
    
    public String refreshAccessToken(String refreshToken) {
        if (validateToken(refreshToken)) {
            String username = getUsernameFromToken(refreshToken);
            return generateTokenFromUsername(username);
        }
        throw new RuntimeException("Invalid refresh token");
    }
}
```

### **4. Token 黑名單**

```java
@Service
public class TokenBlacklistService {
    
    private final Set<String> blacklistedTokens = new ConcurrentHashSet<>();
    
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }
    
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
```

---

## 🧪 **JWT 測試**

### **1. 單元測試**

```java
@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {
    
    @InjectMocks
    private JwtTokenProvider tokenProvider;
    
    @Test
    void whenGenerateToken_thenValidToken() {
        // Given
        String username = "testuser";
        
        // When
        String token = tokenProvider.generateTokenFromUsername(username);
        
        // Then
        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals(username, tokenProvider.getUsernameFromToken(token));
    }
    
    @Test
    void whenTokenExpired_thenInvalid() {
        // Given
        String username = "testuser";
        String token = tokenProvider.generateTokenFromUsername(username);
        
        // When - 等待 Token 過期 (需要設定較短的過期時間進行測試)
        
        // Then
        assertTrue(tokenProvider.isTokenExpired(token));
    }
}
```

### **2. 整合測試**

```java
@SpringBootTest
@AutoConfigureTestDatabase
class JwtAuthenticationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void whenLogin_thenReturnJwtToken() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        
        // When
        ResponseEntity<ApiResponse<LoginResponse>> response = 
            restTemplate.postForEntity("/api/auth/login", loginRequest, 
                new ParameterizedTypeReference<ApiResponse<LoginResponse>>() {});
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getData().getToken());
        assertEquals("Bearer", response.getBody().getData().getTokenType());
    }
    
    @Test
    void whenAccessProtectedEndpointWithValidToken_thenSuccess() {
        // Given
        String token = getValidToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        // When
        ResponseEntity<String> response = 
            restTemplate.exchange("/api/users/1", HttpMethod.GET, entity, String.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
```

---

## 📊 **JWT vs Session 比較**

| 特性 | JWT | Session |
|------|-----|---------|
| **儲存位置** | 客戶端 | 伺服器端 |
| **擴展性** | 高 | 低 |
| **跨域支援** | 是 | 否 |
| **無狀態** | 是 | 否 |
| **安全性** | 中等 | 高 |
| **大小** | 較大 | 較小 |
| **撤銷** | 困難 | 容易 |

---

## ⚠️ **最佳實踐**

### **1. 安全配置**

- ✅ 使用強密鑰 (至少 256 位元)
- ✅ 設定合理的過期時間
- ✅ 使用 HTTPS 傳輸
- ✅ 實作 Token 刷新機制
- ✅ 考慮 Token 黑名單

### **2. 效能優化**

- ✅ 不要在 Token 中儲存過多資訊
- ✅ 使用適當的過期時間
- ✅ 實作 Token 快取機制
- ✅ 監控 Token 大小

### **3. 錯誤處理**

- ✅ 統一的錯誤回應格式
- ✅ 適當的日誌記錄
- ✅ 不要洩露敏感資訊

---

## 📚 **相關資源**

- [JWT 官方網站](https://jwt.io/)
- [RFC 7519 - JSON Web Token](https://tools.ietf.org/html/rfc7519)
- [JJWT 函式庫](https://github.com/jwtk/jjwt)
- [Spring Security JWT 指南](https://spring.io/guides/tutorials/spring-security-and-angular-js/)

---

**記住：JWT 不是萬能的，要根據應用程式需求選擇合適的認證方式！** 🔐
