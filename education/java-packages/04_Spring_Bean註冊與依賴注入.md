# Spring Bean註冊與依賴注入

## 📋 **學習目標**

本文件將詳細解釋Spring Bean註冊的概念、依賴注入的機制，以及為什麼要使用Spring容器來管理物件。

### **學習重點**
- 了解Spring容器的概念和作用
- 學習Bean註冊的過程和方式
- 掌握依賴注入的機制
- 理解Bean的生命週期管理
- 學習不同的Bean作用域

---

## 🎯 **Spring容器概念**

### **什麼是Spring容器？**

Spring容器（ApplicationContext）是一個工廠，負責管理應用程式中的所有物件（Bean）。

```java
// Spring容器就像一個智能工廠
// 我們告訴它需要什麼物件，它會自動建立並管理

// ❌ 沒有Spring容器 - 手動管理物件
public class Main {
    public static void main(String[] args) {
        // 手動建立所有物件
        UserRepository userRepository = new UserRepository();
        RoleRepository roleRepository = new RoleRepository();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        // 手動注入依賴
        UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);
        
        // 手動管理物件的生命週期
        // 手動處理物件之間的依賴關係
        // 容易出錯，難以維護
    }
}

// ✅ 有Spring容器 - 自動管理物件
@SpringBootApplication
public class LibraryApplication {
    public static void main(String[] args) {
        // Spring容器會自動掃描並管理所有Bean
        SpringApplication.run(LibraryApplication.class, args);
    }
}
```

### **為什麼需要Spring容器？**

- ✅ **依賴管理**: 自動處理物件之間的依賴關係
- ✅ **生命週期管理**: 自動管理物件的建立和銷毀
- ✅ **單例管理**: 確保整個應用程式中只有一個實例
- ✅ **配置管理**: 集中管理應用程式的配置
- ✅ **測試便利**: 容易進行單元測試和整合測試

---

## 📝 **Bean註冊過程**

### **1. Bean註冊的三個步驟**

#### **步驟1: 掃描**
```java
@SpringBootApplication  // 告訴Spring要掃描哪些包
public class LibraryApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }
}

// Spring會掃描 com.library 包下的所有類別
// 尋找有 @Service, @Repository, @Controller 等註解的類別
```

#### **步驟2: 建立Bean**
```java
@Service  // Spring發現這個註解，知道要建立Bean
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    // Spring會呼叫建構函式建立物件
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
}

@Repository  // Spring也會建立這個Bean
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring會自動實作這個介面
}
```

#### **步驟3: 管理依賴**
```java
// Spring會自動處理依賴關係
// UserService 需要 UserRepository 和 RoleRepository
// Spring會自動注入這些依賴

@Service
public class UserService {
    private final UserRepository userRepository;      // ← Spring注入
    private final RoleRepository roleRepository;      // ← Spring注入
    
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
}
```

### **2. Bean註冊的時機**

```java
// Spring容器啟動時會執行以下步驟：

// 1. 掃描所有有註解的類別
@ComponentScan("com.library")  // 掃描指定包

// 2. 建立Bean定義
// Spring會分析每個類別的依賴關係

// 3. 按依賴順序建立Bean
// 先建立沒有依賴的Bean，再建立有依賴的Bean

// 4. 注入依賴
// 將已建立的Bean注入到需要的地方

// 5. 初始化Bean
// 呼叫@PostConstruct方法
```

---

## 🔧 **依賴注入機制**

### **1. 什麼是依賴注入？**

依賴注入（Dependency Injection, DI）是一種設計模式，讓物件不需要自己建立依賴，而是由外部提供。

```java
// ❌ 沒有依賴注入 - 物件自己建立依賴
public class UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    
    public UserService() {
        // 物件自己建立依賴 - 耦合度高
        this.userRepository = new UserRepository();
        this.roleRepository = new RoleRepository();
    }
}

// ✅ 有依賴注入 - 外部提供依賴
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    // 依賴由外部（Spring）提供
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
}
```

### **2. 依賴注入的優點**

- ✅ **降低耦合**: 物件不需要知道如何建立依賴
- ✅ **提高可測試性**: 容易進行單元測試
- ✅ **提高可維護性**: 依賴關係清晰明確
- ✅ **提高可重用性**: 物件可以在不同環境中使用

### **3. 依賴注入的方式**

#### **方式1: 建構函式注入（推薦）**
```java
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    // Spring會自動呼叫這個建構函式並注入依賴
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
}
```

#### **方式2: Setter注入**
```java
@Service
public class UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
}
```

#### **方式3: 欄位注入（不推薦）**
```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
}
```

### **4. 為什麼推薦建構函式注入？**

```java
// ✅ 建構函式注入的優點：

// 1. 強制性 - 確保所有依賴都被提供
public UserService(UserRepository userRepository, RoleRepository roleRepository) {
    // 如果缺少任何依賴，編譯時就會發現
}

// 2. 不可變性 - final確保依賴不會被修改
private final UserRepository userRepository;  // 不可修改

// 3. 線程安全 - 不可變的依賴是線程安全的

// 4. 測試便利 - 容易進行單元測試
@Test
void testUserService() {
    UserRepository mockRepo = mock(UserRepository.class);
    RoleRepository mockRoleRepo = mock(RoleRepository.class);
    UserService userService = new UserService(mockRepo, mockRoleRepo);
}

// 5. 循環依賴檢測 - 編譯時就能發現循環依賴
```

---

## 🏗️ **Bean註冊方式**

### **1. 註解方式（推薦）**

#### **@Service - 業務邏輯層**
```java
@Service
public class UserService {
    // 標記為業務邏輯層的Bean
    // Spring會自動掃描並註冊
}
```

#### **@Repository - 資料存取層**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 標記為資料存取層的Bean
    // Spring會自動實作介面
}
```

#### **@Controller - 控制器層**
```java
@Controller
public class UserController {
    // 標記為控制器層的Bean
    // 處理HTTP請求
}
```

#### **@Component - 通用元件**
```java
@Component
public class EmailService {
    // 標記為通用元件的Bean
    // 可以被其他Bean注入使用
}
```

#### **@Configuration - 配置類別**
```java
@Configuration
public class AppConfig {
    // 標記為配置類別的Bean
    // 用於配置其他Bean
}
```

### **2. 配置類別方式**

```java
@Configuration
public class AppConfig {
    
    @Bean
    public UserService userService(UserRepository userRepository, RoleRepository roleRepository) {
        return new UserService(userRepository, roleRepository);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
```

### **3. 條件化Bean註冊**

```java
@Configuration
public class AppConfig {
    
    @Bean
    @ConditionalOnProperty(name = "app.feature.email", havingValue = "true")
    public EmailService emailService() {
        return new EmailService();
    }
    
    @Bean
    @ConditionalOnClass(name = "com.redis.clients.jedis.Jedis")
    public RedisService redisService() {
        return new RedisService();
    }
    
    @Bean
    @Profile("dev")
    public DevConfig devConfig() {
        return new DevConfig();
    }
    
    @Bean
    @Profile("prod")
    public ProdConfig prodConfig() {
        return new ProdConfig();
    }
}
```

---

## 🔄 **Bean生命週期**

### **1. Bean生命週期的階段**

```java
@Service
public class UserService {
    
    // 1. 實例化 - Spring呼叫建構函式
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    // 2. 屬性注入 - Spring注入依賴
    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    // 3. 初始化 - Spring呼叫@PostConstruct
    @PostConstruct
    public void init() {
        System.out.println("UserService已初始化");
        // 執行初始化邏輯
    }
    
    // 4. 使用 - Bean可以被其他Bean使用
    
    // 5. 銷毀 - Spring呼叫@PreDestroy
    @PreDestroy
    public void cleanup() {
        System.out.println("UserService即將銷毀");
        // 執行清理邏輯
    }
}
```

### **2. 生命週期回調**

```java
@Service
public class DatabaseService implements InitializingBean, DisposableBean {
    
    // 實作InitializingBean介面
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("所有屬性設定完成後呼叫");
        // 初始化資料庫連線
    }
    
    // 實作DisposableBean介面
    @Override
    public void destroy() throws Exception {
        System.out.println("Bean銷毀前呼叫");
        // 關閉資料庫連線
    }
}
```

---

## 🎯 **Bean作用域**

### **1. Singleton（單例）- 預設**
```java
@Service
@Scope("singleton")  // 預設作用域
public class UserService {
    // 整個應用程式中只有一個實例
    // 所有地方使用的都是同一個物件
}

// 使用範例
@Controller
public class UserController1 {
    private final UserService userService; // 同一個實例
}

@Controller  
public class UserController2 {
    private final UserService userService; // 同一個實例
}
```

### **2. Prototype（原型）**
```java
@Service
@Scope("prototype")
public class NotificationService {
    // 每次注入都建立新實例
    // 每個地方使用的都是不同的物件
}

// 使用範例
@Controller
public class UserController1 {
    private final NotificationService notificationService; // 新實例
}

@Controller  
public class UserController2 {
    private final NotificationService notificationService; // 新實例
}
```

### **3. Request（請求）**
```java
@Service
@Scope("request")
public class RequestService {
    // 每個HTTP請求一個實例
    // 請求結束後自動銷毀
}
```

### **4. Session（會話）**
```java
@Service
@Scope("session")
public class SessionService {
    // 每個HTTP會話一個實例
    // 會話結束後自動銷毀
}
```

### **5. Application（應用程式）**
```java
@Service
@Scope("application")
public class ApplicationService {
    // 整個Web應用程式一個實例
    // 類似Singleton，但作用域更明確
}
```

---

## ⚠️ **注意事項**

### **1. 循環依賴**
```java
// ❌ 循環依賴 - 會導致錯誤
@Service
public class UserService {
    private final RoleService roleService;
    
    public UserService(RoleService roleService) {
        this.roleService = roleService;
    }
}

@Service
public class RoleService {
    private final UserService userService;
    
    public RoleService(UserService userService) {
        this.userService = userService;
    }
}

// ✅ 解決方案1: 使用@Lazy
@Service
public class UserService {
    private final RoleService roleService;
    
    public UserService(@Lazy RoleService roleService) {
        this.roleService = roleService;
    }
}

// ✅ 解決方案2: 重新設計架構
// 將共同邏輯抽取到第三個Service中
```

### **2. Bean名稱衝突**
```java
// 如果有多個相同類型的Bean，需要指定名稱
@Service("userService")
public class UserService {
    // 指定Bean名稱為userService
}

@Service("adminUserService")
public class AdminUserService {
    // 指定Bean名稱為adminUserService
}

// 注入時指定Bean名稱
@Controller
public class UserController {
    private final UserService userService;
    
    public UserController(@Qualifier("userService") UserService userService) {
        this.userService = userService;
    }
}
```

### **3. 條件化Bean**
```java
@Configuration
public class AppConfig {
    
    @Bean
    @ConditionalOnProperty(name = "app.database.type", havingValue = "mysql")
    public DataSource mysqlDataSource() {
        return new MysqlDataSource();
    }
    
    @Bean
    @ConditionalOnProperty(name = "app.database.type", havingValue = "postgresql")
    public DataSource postgresqlDataSource() {
        return new PostgresqlDataSource();
    }
}
```

---

## 🧪 **測試Bean**

### **1. 單元測試**
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void testRegisterUser() {
        // Given
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(new Role()));
        when(userRepository.save(any(User.class))).thenReturn(new User());
        
        // When
        User result = userService.registerUser("test", "test@example.com", "password", "Test User");
        
        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }
}
```

### **2. 整合測試**
```java
@SpringBootTest
@Transactional
class UserServiceIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testRegisterUser() {
        // Given
        String username = "testuser";
        
        // When
        User user = userService.registerUser(username, "test@example.com", "password", "Test User");
        
        // Then
        assertNotNull(user.getId());
        assertTrue(userRepository.existsByUsername(username));
    }
}
```

---

## 📚 **總結**

### **Bean註冊的核心概念**
1. **Spring容器**: 管理所有物件的工廠
2. **Bean註冊**: 將物件交給Spring容器管理
3. **依賴注入**: 由Spring自動提供物件所需的依賴
4. **生命週期**: Spring管理物件的建立和銷毀
5. **作用域**: 控制Bean的實例數量

### **最佳實踐**
- ✅ 使用建構函式注入
- ✅ 使用註解方式註冊Bean
- ✅ 避免循環依賴
- ✅ 合理使用Bean作用域
- ✅ 編寫完整的測試

### **學習檢查清單**
- [ ] 了解Spring容器的概念
- [ ] 掌握Bean註冊的過程
- [ ] 理解依賴注入的機制
- [ ] 學會不同的Bean註冊方式
- [ ] 了解Bean的生命週期
- [ ] 掌握Bean的作用域
- [ ] 學會處理常見問題
- [ ] 能夠編寫Bean的測試

---

**現在您已經了解了Spring Bean註冊與依賴注入的核心概念！**
