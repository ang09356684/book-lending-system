# Java套件學習 - Spring Security

## 📋 **什麼是 Spring Security？**

Spring Security 是 Spring 框架的安全模組，提供企業級應用程式的安全功能。它是 Spring 生態系統中最重要的安全框架。

### **為什麼需要 Spring Security？**

在現代 Web 應用程式中，安全性是最重要的考量之一：
- **認證 (Authentication)**: 確認使用者身份
- **授權 (Authorization)**: 控制使用者可以存取哪些資源
- **會話管理**: 管理使用者登入狀態
- **密碼加密**: 安全地儲存密碼
- **CSRF 防護**: 防止跨站請求偽造攻擊
- **XSS 防護**: 防止跨站腳本攻擊

---

## 🎯 **Spring Security 核心概念**

### **1. 認證 (Authentication)**

認證是確認使用者身份的過程：

```java
// 使用者登入流程
UsernamePasswordAuthenticationToken authRequest = 
    new UsernamePasswordAuthenticationToken(username, password);

Authentication authentication = authenticationManager.authenticate(authRequest);
SecurityContextHolder.getContext().setAuthentication(authentication);
```

### **2. 授權 (Authorization)**

授權是決定使用者可以存取哪些資源的過程：

```java
// 檢查使用者是否有特定角色
@PreAuthorize("hasRole('ADMIN')")
public void adminOnlyMethod() {
    // 只有 ADMIN 角色才能執行
}

// 檢查使用者是否有特定權限
@PreAuthorize("hasAuthority('READ_BOOKS')")
public void readBooks() {
    // 只有 READ_BOOKS 權限才能執行
}
```

### **3. 使用者詳情 (UserDetails)**

Spring Security 使用 `UserDetails` 介面來表示使用者：

```java
public interface UserDetails {
    String getUsername();
    String getPassword();
    Collection<? extends GrantedAuthority> getAuthorities();
    boolean isAccountNonExpired();
    boolean isAccountNonLocked();
    boolean isCredentialsNonExpired();
    boolean isEnabled();
}
```

---

## 🏗️ **Spring Security 架構**

### **Filter Chain 架構**

```
HTTP Request → Security Filter Chain → Authentication → Authorization → Response
```

1. **SecurityFilterChain**: 過濾器鏈，處理所有請求
2. **AuthenticationManager**: 管理認證過程
3. **UserDetailsService**: 載入使用者資訊
4. **AccessDecisionManager**: 決定是否授權存取

### **核心組件**

#### **1. SecurityFilterChain**
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .formLogin(form -> form.loginPage("/login"))
        .logout(logout -> logout.logoutUrl("/logout"));
    
    return http.build();
}
```

#### **2. AuthenticationManager**
```java
@Bean
public AuthenticationManager authenticationManager(
    AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
}
```

#### **3. UserDetailsService**
```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Override
    public UserDetails loadUserByUsername(String username) 
        throws UsernameNotFoundException {
        // 從資料庫載入使用者資訊
        User user = userRepository.findByUsername(username);
        return createUserDetails(user);
    }
}
```

---

## 🔐 **JWT 整合**

### **JWT Token Provider**

```java
@Component
public class JwtTokenProvider {
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;
    
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        return Jwts.builder()
            .setSubject(userPrincipal.getUsername())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

### **JWT 認證過濾器**

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
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
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
}
```

---

## 🔑 **權限控制**

### **角色基礎權限控制 (RBAC)**

#### **1. 角色定義**
```java
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    
    // getters and setters
}
```

#### **2. 使用者角色關聯**
```java
@Entity
@Table(name = "users")
public class User {
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    
    // other fields
}
```

#### **3. 權限配置**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/books/**").permitAll()
                
                // User endpoints
                .requestMatchers("/api/borrows/**").hasRole("USER")
                .requestMatchers("/api/users/**").hasRole("USER")
                
                // Librarian endpoints
                .requestMatchers("/api/librarian/**").hasRole("LIBRARIAN")
                
                // Admin endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
```

### **方法級別安全**

#### **1. @PreAuthorize**
```java
@Service
public class BookService {
    
    @PreAuthorize("hasRole('LIBRARIAN')")
    public Book createBook(String title, String author) {
        // Only librarians can create books
    }
    
    @PreAuthorize("hasRole('USER') or hasRole('LIBRARIAN')")
    public List<Book> searchBooks(String keyword) {
        // Both users and librarians can search books
    }
}
```

#### **2. @PostAuthorize**
```java
@Service
public class UserService {
    
    @PostAuthorize("returnObject.username == authentication.name or hasRole('ADMIN')")
    public User getUserById(Long id) {
        // Users can only access their own data, admins can access all
        return userRepository.findById(id).orElse(null);
    }
}
```

---

## 🔧 **密碼加密**

### **BCrypt 密碼加密**

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

@Service
public class UserService {
    
    private final PasswordEncoder passwordEncoder;
    
    public User registerUser(String username, String password, String email) {
        // Encrypt password before saving
        String encodedPassword = passwordEncoder.encode(password);
        
        User user = new User(username, encodedPassword, email);
        return userRepository.save(user);
    }
    
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
```

### **密碼加密演算法比較**

| 演算法 | 安全性 | 速度 | 鹽值 | 推薦度 |
|--------|--------|------|------|--------|
| BCrypt | 高 | 慢 | 自動 | ⭐⭐⭐⭐⭐ |
| SCrypt | 很高 | 很慢 | 自動 | ⭐⭐⭐⭐ |
| Argon2 | 最高 | 中等 | 自動 | ⭐⭐⭐⭐⭐ |
| SHA-256 | 低 | 快 | 手動 | ❌ |

---

## 🛡️ **安全防護**

### **1. CSRF 防護**

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // 對於 API 應用程式
            // 或者
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        
        return http.build();
    }
}
```

### **2. 會話管理**

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // JWT 應用程式
                // 或者
                .maximumSessions(1)  // 限制同時登入數量
                .expiredUrl("/login?expired")
            );
        
        return http.build();
    }
}
```

### **3. 登出處理**

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    // 清除 JWT token
                    response.setStatus(HttpStatus.OK.value());
                })
                .invalidateHttpSession(true)
                .clearAuthentication(true)
            );
        
        return http.build();
    }
}
```

---

## 🧪 **測試 Spring Security**

### **1. 單元測試**

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void whenRegisterUser_thenPasswordShouldBeEncoded() {
        // Given
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(new User());
        
        // When
        userService.registerUser("testuser", "test@email.com", rawPassword, "Test User");
        
        // Then
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(argThat(user -> 
            user.getPassword().equals(encodedPassword)
        ));
    }
}
```

### **2. 整合測試**

```java
@SpringBootTest
@AutoConfigureTestDatabase
class SecurityIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void whenAccessProtectedEndpointWithoutToken_thenUnauthorized() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/users/1", String.class);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    void whenAccessProtectedEndpointWithValidToken_thenSuccess() {
        // First login to get token
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        ResponseEntity<ApiResponse<LoginResponse>> loginResponse = 
            restTemplate.postForEntity("/api/auth/login", loginRequest, 
                new ParameterizedTypeReference<ApiResponse<LoginResponse>>() {});
        
        String token = loginResponse.getBody().getData().getToken();
        
        // Use token to access protected endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange("/api/users/1", 
            HttpMethod.GET, entity, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
```

---

## ⚠️ **最佳實踐**

### **1. 安全配置**

- ✅ 使用強密碼策略
- ✅ 實作帳戶鎖定機制
- ✅ 設定密碼過期時間
- ✅ 使用 HTTPS
- ✅ 實作登入嘗試限制

### **2. JWT 安全**

- ✅ 使用強密鑰
- ✅ 設定合理的過期時間
- ✅ 實作 Token 刷新機制
- ✅ 在客戶端安全儲存 Token

### **3. 錯誤處理**

- ✅ 不要洩露敏感資訊
- ✅ 統一的錯誤回應格式
- ✅ 適當的日誌記錄

### **4. 監控和審計**

- ✅ 記錄登入嘗試
- ✅ 監控異常活動
- ✅ 定期安全審計

---

## 📚 **相關資源**

- [Spring Security 官方文件](https://docs.spring.io/spring-security/reference/)
- [Spring Security 參考指南](https://docs.spring.io/spring-security/site/docs/current/reference/html5/)
- [JWT 官方網站](https://jwt.io/)
- [OWASP 安全指南](https://owasp.org/www-project-top-ten/)

---

**記住：安全性不是一次性工作，而是持續的過程！** 🔒
