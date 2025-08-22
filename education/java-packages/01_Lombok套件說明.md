# Java套件學習 - Lombok

## 📋 **什麼是 Lombok？**

Lombok 是一個Java程式庫，用來**自動生成**常用的程式碼，讓開發者可以寫更簡潔的程式碼。

### **為什麼需要 Lombok？**

在Java中，我們經常需要為類別的每個欄位寫：
- Getter 方法 (取得值)
- Setter 方法 (設定值)  
- 建構函式 (Constructor)
- toString() 方法
- equals() 和 hashCode() 方法

這些程式碼很重複且無聊，Lombok 可以幫我們自動生成！

---

## 🎯 **Lombok 的主要功能**

### **1. 自動生成 Getter/Setter**

#### **傳統寫法 (沒有 Lombok)：**
```java
public class User {
    private String name;
    private String email;
    private Integer age;
    
    // Getter 方法
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public Integer getAge() {
        return age;
    }
    
    // Setter 方法
    public void setName(String name) {
        this.name = name;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
}
```

#### **使用 Lombok：**
```java
@Data
public class User {
    private String name;
    private String email;
    private Integer age;
    // 只需要 @Data 註解，Lombok 會自動生成所有 getter/setter
}
```

**節省的程式碼行數：** 從 30+ 行減少到 6 行！

### **2. 自動生成建構函式**

#### **傳統寫法：**
```java
public class User {
    private String name;
    private String email;
    
    // 預設建構函式
    public User() {
    }
    
    // 全參數建構函式
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
```

#### **使用 Lombok：**
```java
@NoArgsConstructor  // 自動生成預設建構函式
@AllArgsConstructor // 自動生成全參數建構函式
public class User {
    private String name;
    private String email;
}
```

### **3. 自動生成 toString()**

#### **傳統寫法：**
```java
@Override
public String toString() {
    return "User{" +
           "name='" + name + '\'' +
           ", email='" + email + '\'' +
           ", age=" + age +
           '}';
}
```

#### **使用 Lombok：**
```java
@ToString // 自動生成 toString()
public class User {
    private String name;
    private String email;
    private Integer age;
}
```

---

## 📝 **常用的 Lombok 註解**

### **@Data**
最常用的註解，包含：
- 所有欄位的 getter/setter
- toString() 方法
- equals() 方法
- hashCode() 方法
- 預設建構函式

```java
@Data
public class User {
    private String name;
    private String email;
}
```

### **@Getter / @Setter**
只生成 getter 或 setter 方法

```java
@Getter
@Setter
public class User {
    private String name;
    private String email;
}
```

### **@NoArgsConstructor**
生成無參數的建構函式

```java
@NoArgsConstructor
public class User {
    private String name;
    private String email;
}
```

### **@AllArgsConstructor**
生成包含所有欄位的建構函式

```java
@AllArgsConstructor
public class User {
    private String name;
    private String email;
}
```

### **@RequiredArgsConstructor**
生成包含 final 欄位或 @NonNull 欄位的建構函式

```java
@RequiredArgsConstructor
public class User {
    private final String name;  // final 欄位
    private String email;       // 一般欄位
}
```

### **@ToString**
生成 toString() 方法

```java
@ToString
public class User {
    private String name;
    private String email;
}
```

### **@EqualsAndHashCode**
生成 equals() 和 hashCode() 方法

```java
@EqualsAndHashCode
public class User {
    private String name;
    private String email;
}
```

### **@Builder**
生成建造者模式，方便建立物件

```java
@Builder
public class User {
    private String name;
    private String email;
    private Integer age;
}

// 使用方式：
User user = User.builder()
    .name("John")
    .email("john@example.com")
    .age(25)
    .build();
```

---

## 🔧 **在我們的專案中使用 Lombok**

### **1. 添加依賴**
在 `pom.xml` 中添加：
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

### **2. 在 Entity 類別中使用**
```java
@Entity
@Table(name = "users")
@Data           // 自動生成 getter/setter/toString/equals/hashCode
@NoArgsConstructor // 自動生成預設建構函式
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "email")
    private String email;
    
    // 不需要手寫任何 getter/setter！
}
```

### **3. IDE 設定**
如果使用 IntelliJ IDEA：
1. 安裝 Lombok 插件
2. 啟用 Annotation Processing

如果使用 VS Code：
1. 安裝 Java Extension Pack
2. 安裝 Lombok Annotations Support

---

## ⚠️ **注意事項**

### **優點：**
✅ **大幅減少程式碼量** - 從幾十行減少到幾行  
✅ **提高開發效率** - 專注在業務邏輯  
✅ **減少錯誤** - 自動生成的程式碼不會有錯誤  
✅ **程式碼更簡潔** - 易讀易維護  

### **缺點：**
❌ **需要 IDE 支援** - 需要安裝插件  
❌ **學習成本** - 需要了解註解的含義  
❌ **編譯時生成** - 在編譯時才生成程式碼  
❌ **可能過度使用** - 不是所有情況都適合使用  

### **使用建議：**
1. **適合使用 Lombok 的情況：**
   - Entity 類別 (資料庫對應)
   - DTO 類別 (資料傳輸物件)
   - 簡單的 POJO 類別

2. **不適合使用 Lombok 的情況：**
   - 需要自定義邏輯的 getter/setter
   - 複雜的業務邏輯類別
   - 需要特殊處理的建構函式

---

## 🧪 **練習範例**

### **練習 1：建立簡單的 User 類別**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
    private Integer age;
}
```

### **練習 2：建立 Book 類別**
```java
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Book {
    private Long id;
    private String title;
    private String author;
    private Integer publishedYear;
}
```

### **練習 3：使用 Builder 模式**
```java
@Builder
@Data
public class Library {
    private Long id;
    private String name;
    private String address;
    private String phone;
}

// 使用方式：
Library library = Library.builder()
    .name("Main Library")
    .address("Taipei City")
    .phone("02-1234-5678")
    .build();
```

---

## 📚 **相關資源**

- [Lombok 官方網站](https://projectlombok.org/)
- [Lombok 註解說明](https://projectlombok.org/features/all)
- [IntelliJ IDEA Lombok 插件](https://plugins.jetbrains.com/plugin/6317-lombok)

---

**記住：Lombok 是一個工具，目的是讓程式碼更簡潔，但不要過度依賴它！**
