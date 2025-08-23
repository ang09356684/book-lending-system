# Java套件學習 - Optional 方法詳解

## 📋 **什麼是 Optional？**

`Optional` 是 Java 8 引入的一個容器類別，用來表示一個值可能存在也可能不存在。它是一個用來避免 `NullPointerException` 的工具類別。

### **為什麼需要 Optional？**

在傳統的 Java 程式碼中，我們經常需要檢查 null 值：

```java
// ❌ 傳統寫法 - 容易出錯
public String getUserName(User user) {
    if (user != null) {
        if (user.getName() != null) {
            return user.getName();
        }
    }
    return "Unknown";
}
```

使用 Optional 可以更優雅地處理：

```java
// ✅ Optional 寫法 - 更安全、更簡潔
public String getUserName(User user) {
    return Optional.ofNullable(user)
        .map(User::getName)
        .orElse("Unknown");
}
```

---

## 🎯 **Optional 的建立方法**

### **1. 建立包含值的 Optional**

```java
// 建立包含非空值的 Optional
Optional<String> name = Optional.of("John");
Optional<Integer> age = Optional.of(25);

// 注意：如果傳入 null，會拋出 NullPointerException
// Optional<String> nullName = Optional.of(null); // ❌ 會拋出例外
```

### **2. 建立可能為空的 Optional**

```java
// 建立可能為空的 Optional
Optional<String> name = Optional.ofNullable("John");
Optional<String> nullName = Optional.ofNullable(null); // ✅ 不會拋出例外
```

### **3. 建立空的 Optional**

```java
// 建立空的 Optional
Optional<String> emptyName = Optional.empty();
```

---

## 🔧 **Optional 的核心方法**

### **1. 檢查值是否存在**

#### **isPresent()**
```java
Optional<String> name = Optional.of("John");

if (name.isPresent()) {
    System.out.println("Name is: " + name.get());
} else {
    System.out.println("Name is not present");
}
```

#### **isEmpty()** (Java 11+)
```java
Optional<String> name = Optional.empty();

if (name.isEmpty()) {
    System.out.println("Name is empty");
} else {
    System.out.println("Name is: " + name.get());
}
```

### **2. 安全地取得值**

#### **get()**
```java
Optional<String> name = Optional.of("John");
String value = name.get(); // 直接取得值

// ⚠️ 注意：如果 Optional 為空，會拋出 NoSuchElementException
Optional<String> emptyName = Optional.empty();
// String value = emptyName.get(); // ❌ 會拋出例外
```

#### **orElse(T other)**
```java
Optional<String> name = Optional.of("John");
String value1 = name.orElse("Unknown"); // "John"

Optional<String> emptyName = Optional.empty();
String value2 = emptyName.orElse("Unknown"); // "Unknown"
```

#### **orElseGet(Supplier<T> supplier)**
```java
Optional<String> name = Optional.of("John");
String value1 = name.orElseGet(() -> generateDefaultName()); // "John"

Optional<String> emptyName = Optional.empty();
String value2 = emptyName.orElseGet(() -> generateDefaultName()); // 調用方法生成預設值

private String generateDefaultName() {
    return "Default User " + System.currentTimeMillis();
}
```

#### **orElseThrow(Supplier<X> exceptionSupplier)**
```java
Optional<String> name = Optional.of("John");
String value1 = name.orElseThrow(() -> new RuntimeException("Name not found")); // "John"

Optional<String> emptyName = Optional.empty();
String value2 = emptyName.orElseThrow(() -> new RuntimeException("Name not found")); // 拋出例外
```

### **3. 條件處理**

#### **ifPresent(Consumer<T> consumer)**
```java
Optional<String> name = Optional.of("John");

// 如果值存在，執行指定的操作
name.ifPresent(value -> System.out.println("Hello, " + value)); // 輸出: Hello, John

Optional<String> emptyName = Optional.empty();
emptyName.ifPresent(value -> System.out.println("Hello, " + value)); // 不執行任何操作
```

#### **ifPresentOrElse(Consumer<T> consumer, Runnable emptyAction)** (Java 9+)
```java
Optional<String> name = Optional.of("John");

name.ifPresentOrElse(
    value -> System.out.println("Hello, " + value),  // 值存在時執行
    () -> System.out.println("Name not found")       // 值不存在時執行
);

Optional<String> emptyName = Optional.empty();
emptyName.ifPresentOrElse(
    value -> System.out.println("Hello, " + value),
    () -> System.out.println("Name not found")       // 輸出: Name not found
);
```

### **4. 轉換和過濾**

#### **map(Function<T, U> mapper)**
```java
Optional<String> name = Optional.of("John");

// 轉換值
Optional<Integer> nameLength = name.map(String::length); // Optional[4]
Optional<String> upperName = name.map(String::toUpperCase); // Optional["JOHN"]

// 鏈式調用
Optional<String> result = name
    .map(String::toUpperCase)
    .map(s -> "Hello, " + s); // Optional["Hello, JOHN"]
```

#### **flatMap(Function<T, Optional<U>> mapper)**
```java
Optional<String> name = Optional.of("John");

// 轉換為另一個 Optional
Optional<String> result = name.flatMap(n -> Optional.of("Hello, " + n)); // Optional["Hello, John"]

// 複雜範例：處理嵌套的 Optional
Optional<User> user = Optional.of(new User("John", "john@email.com"));
Optional<String> email = user.flatMap(User::getEmail); // 假設 getEmail() 返回 Optional<String>
```

#### **filter(Predicate<T> predicate)**
```java
Optional<String> name = Optional.of("John");

// 過濾值
Optional<String> longName = name.filter(n -> n.length() > 3); // Optional["John"]
Optional<String> shortName = name.filter(n -> n.length() < 3); // Optional.empty()

// 實際應用範例
Optional<User> user = Optional.of(new User("John", 25));
Optional<User> adultUser = user.filter(u -> u.getAge() >= 18); // 只保留成年使用者
```

---

## 🏗️ **實際應用範例**

### **1. 在我們的專案中的應用**

#### **使用者查詢**
```java
// 在 AuthController 中
User user = userService.findByUsername(request.getUsername())
    .orElseThrow(() -> new RuntimeException("User not found"));

// 在 UserService 中
public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
}
```

#### **書籍查詢**
```java
// 在 BookController 中
Book book = bookService.findById(id)
    .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

// 安全地取得書籍資訊
String bookTitle = bookService.findById(id)
    .map(Book::getTitle)
    .orElse("Unknown Title");
```

#### **圖書館查詢**
```java
// 在 LibraryController 中
Library library = libraryService.findById(id)
    .orElseThrow(() -> new RuntimeException("Library not found with id: " + id));

// 條件處理
libraryService.findById(id).ifPresent(lib -> {
    System.out.println("Found library: " + lib.getName());
    // 執行其他操作
});
```

### **2. 複雜的業務邏輯處理**

#### **使用者註冊驗證**
```java
public boolean validateUserRegistration(String username, String email) {
    return Optional.ofNullable(username)
        .filter(name -> !name.trim().isEmpty())
        .filter(name -> name.length() >= 3)
        .flatMap(name -> Optional.ofNullable(email))
        .filter(email -> email.contains("@"))
        .isPresent();
}
```

#### **安全的字串處理**
```java
public String getDisplayName(User user) {
    return Optional.ofNullable(user)
        .map(User::getName)
        .filter(name -> !name.trim().isEmpty())
        .orElseGet(() -> user.getUsername())
        .orElse("Anonymous");
}
```

#### **配置值處理**
```java
public int getPort() {
    return Optional.ofNullable(System.getProperty("server.port"))
        .map(Integer::parseInt)
        .filter(port -> port > 0 && port <= 65535)
        .orElse(8080);
}
```

---

## 🔄 **Optional 鏈式操作**

### **1. 基本鏈式操作**
```java
Optional<String> result = Optional.of("John")
    .map(String::toUpperCase)
    .map(s -> "Hello, " + s)
    .filter(s -> s.length() > 10)
    .orElse("Default Message");
```

### **2. 複雜的業務邏輯鏈**
```java
public String processUser(User user) {
    return Optional.ofNullable(user)
        .filter(u -> u.isActive())
        .map(User::getProfile)
        .map(Profile::getDisplayName)
        .filter(name -> !name.trim().isEmpty())
        .map(String::trim)
        .orElse("Anonymous User");
}
```

### **3. 錯誤處理鏈**
```java
public UserResult processUserRequest(String username) {
    return Optional.ofNullable(username)
        .filter(name -> !name.trim().isEmpty())
        .flatMap(name -> userService.findByUsername(name))
        .filter(User::isVerified)
        .map(user -> new UserResult(user, "Success"))
        .orElse(new UserResult(null, "User not found or not verified"));
}
```

---

## ⚠️ **常見陷阱和最佳實踐**

### **1. 避免的陷阱**

#### **❌ 不要這樣做：**
```java
// 1. 不要使用 get() 而不檢查
Optional<String> name = Optional.of("John");
String value = name.get(); // 如果確定有值可以，但通常不推薦

// 2. 不要這樣檢查 null
if (optional.isPresent() && optional.get() != null) {
    // 這樣做是多餘的
}

// 3. 不要這樣處理
Optional<String> name = Optional.of("John");
if (name.isPresent()) {
    String value = name.get();
    // 處理 value
} else {
    // 處理空值
}
```

#### **✅ 推薦的做法：**
```java
// 1. 使用 orElse/orElseGet/orElseThrow
String value = optional.orElse("default");

// 2. 使用 ifPresent
optional.ifPresent(value -> {
    // 處理 value
});

// 3. 使用 map/flatMap 進行轉換
Optional<String> result = optional
    .map(String::toUpperCase)
    .filter(s -> s.length() > 3);
```

### **2. 最佳實踐**

#### **1. 優先使用函數式風格**
```java
// ✅ 函數式風格
return Optional.ofNullable(user)
    .map(User::getName)
    .orElse("Unknown");

// ❌ 命令式風格
if (user != null) {
    String name = user.getName();
    if (name != null) {
        return name;
    }
}
return "Unknown";
```

#### **2. 使用適當的預設值**
```java
// 使用 orElse 當預設值是簡單值
String name = optional.orElse("Unknown");

// 使用 orElseGet 當預設值需要計算
String name = optional.orElseGet(() -> generateDefaultName());

// 使用 orElseThrow 當沒有值時應該拋出例外
String name = optional.orElseThrow(() -> new IllegalArgumentException("Name is required"));
```

#### **3. 組合多個 Optional**
```java
// 使用 flatMap 處理嵌套的 Optional
Optional<String> result = userOptional
    .flatMap(User::getProfile)
    .flatMap(Profile::getEmail);

// 使用 Optional.ofNullable 處理可能為 null 的值
Optional<String> name = Optional.ofNullable(user.getName());
```

---

## 📊 **Optional 方法總結表**

| 方法 | 返回類型 | 描述 | 使用場景 |
|------|----------|------|----------|
| `of(T value)` | `Optional<T>` | 建立包含非空值的 Optional | 確定值不為 null |
| `ofNullable(T value)` | `Optional<T>` | 建立可能為空的 Optional | 值可能為 null |
| `empty()` | `Optional<T>` | 建立空的 Optional | 表示沒有值 |
| `isPresent()` | `boolean` | 檢查是否有值 | 條件判斷 |
| `isEmpty()` | `boolean` | 檢查是否為空 | 條件判斷 (Java 11+) |
| `get()` | `T` | 取得值 | 確定有值時 |
| `orElse(T other)` | `T` | 返回值或預設值 | 有簡單預設值 |
| `orElseGet(Supplier<T> supplier)` | `T` | 返回值或計算的預設值 | 預設值需要計算 |
| `orElseThrow(Supplier<X> exceptionSupplier)` | `T` | 返回值或拋出例外 | 沒有值時拋出例外 |
| `ifPresent(Consumer<T> consumer)` | `void` | 如果存在值則執行操作 | 條件執行 |
| `ifPresentOrElse(Consumer<T>, Runnable)` | `void` | 條件執行或執行空操作 | 條件執行 (Java 9+) |
| `map(Function<T, U> mapper)` | `Optional<U>` | 轉換值 | 值轉換 |
| `flatMap(Function<T, Optional<U>> mapper)` | `Optional<U>` | 轉換為另一個 Optional | 嵌套 Optional 處理 |
| `filter(Predicate<T> predicate)` | `Optional<T>` | 過濾值 | 條件過濾 |

---

## 🧪 **練習範例**

### **練習 1：基本操作**
```java
Optional<String> name = Optional.of("John");
Optional<Integer> age = Optional.of(25);
Optional<String> emptyName = Optional.empty();

// 1. 檢查值是否存在
System.out.println(name.isPresent()); // true
System.out.println(emptyName.isEmpty()); // true

// 2. 安全地取得值
String value1 = name.orElse("Unknown"); // "John"
String value2 = emptyName.orElse("Unknown"); // "Unknown"

// 3. 條件處理
name.ifPresent(value -> System.out.println("Hello, " + value)); // Hello, John
emptyName.ifPresent(value -> System.out.println("Hello, " + value)); // 不執行
```

### **練習 2：轉換和過濾**
```java
Optional<String> name = Optional.of("John");

// 轉換
Optional<String> upperName = name.map(String::toUpperCase); // Optional["JOHN"]
Optional<Integer> nameLength = name.map(String::length); // Optional[4]

// 過濾
Optional<String> longName = name.filter(n -> n.length() > 3); // Optional["John"]
Optional<String> shortName = name.filter(n -> n.length() < 3); // Optional.empty()

// 鏈式操作
String result = name
    .map(String::toUpperCase)
    .map(s -> "Hello, " + s)
    .orElse("Default"); // "Hello, JOHN"
```

### **練習 3：實際應用**
```java
// 模擬使用者查詢
public String getUserDisplayName(String username) {
    return userService.findByUsername(username)
        .map(User::getName)
        .filter(name -> !name.trim().isEmpty())
        .orElseGet(() -> userService.findByUsername(username)
            .map(User::getUsername)
            .orElse("Anonymous"));
}
```

---

## 📚 **相關資源**

- [Java 8 Optional 官方文件](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html)
- [Java 9 Optional 增強功能](https://docs.oracle.com/javase/9/docs/api/java/util/Optional.html)
- [Java 11 Optional 增強功能](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html)

---

**記住：Optional 是處理 null 值的強大工具，但要用得當！** 🎯
