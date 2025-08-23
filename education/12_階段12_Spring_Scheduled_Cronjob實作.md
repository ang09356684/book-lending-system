# 階段12: Spring Scheduled Cronjob 實作

## 📋 **學習目標**

本階段將學習如何使用 Spring 的 `@Scheduled` 註解實作定時任務，建立自動化的業務流程，如逾期通知檢查、資料清理等。

### **學習重點**
- 了解 Spring Scheduled 的作用和優勢
- 學習 `@Scheduled` 註解的使用方法
- 掌握 Cron 表達式的語法
- 建立完整的定時任務系統

---

## 🎯 **Spring Scheduled 簡介**

### **什麼是 Spring Scheduled？**
Spring Scheduled 是 Spring Framework 提供的定時任務框架，讓我們可以輕鬆地建立和執行定時任務，無需額外的外部依賴。

### **為什麼使用 Spring Scheduled？**
- ✅ **內建於 Spring**: 不需要額外的依賴或容器
- ✅ **簡單易用**: 只需要 `@Scheduled` 註解
- ✅ **多種執行方式**: 支援 cron、fixedRate、fixedDelay
- ✅ **整合性好**: 與 Spring Boot 完美整合
- ✅ **線程池管理**: 自動管理執行線程

### **與其他方案的比較**

| 方案 | 優點 | 缺點 | 適用場景 |
|------|------|------|----------|
| **Spring Scheduled** | 簡單、內建、整合好 | 功能相對簡單 | 中小型專案 |
| **Quartz** | 功能強大、支援集群 | 配置複雜、依賴多 | 大型企業專案 |
| **外部 Cron** | 系統級別、獨立執行 | 需要額外配置、整合困難 | 系統級任務 |

---

## 🔧 **環境配置**

### **步驟1: 啟用排程功能**

在主應用程式類別中添加 `@EnableScheduling`：

```java
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling  // 啟用排程功能
public class LibraryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
    }
}
```

### **步驟2: 建立排程配置類別**

```java
@Configuration
@EnableScheduling
public class SchedulingConfig {
    
    /**
     * Configure thread pool task scheduler for scheduled tasks
     * 
     * @return ThreadPoolTaskScheduler with custom configuration
     */
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        
        // Set pool size for scheduled tasks
        scheduler.setPoolSize(5);
        
        // Set thread name prefix for easy identification
        scheduler.setThreadNamePrefix("scheduled-task-");
        
        // Set wait for tasks to complete on shutdown
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        
        // Set await termination period (in seconds)
        scheduler.setAwaitTerminationSeconds(30);
        
        // Initialize the scheduler
        scheduler.initialize();
        
        return scheduler;
    }
}
```

---

## 📝 **@Scheduled 註解使用**

### **1. Cron 表達式方式**

#### **基本語法**
```java
@Scheduled(cron = "秒 分 時 日 月 星期")
```

#### **Cron 表達式格式**
| 欄位 | 說明 | 範圍 | 特殊字符 |
|------|------|------|----------|
| 秒 | 秒數 | 0-59 | `* / , - ?` |
| 分 | 分鐘 | 0-59 | `* / , - ?` |
| 時 | 小時 | 0-23 | `* / , - ?` |
| 日 | 日期 | 1-31 | `* / , - ? L W` |
| 月 | 月份 | 1-12 | `* / , - ?` |
| 星期 | 星期幾 | 0-7 (0和7都代表星期日) | `* / , - ? L #` |

#### **常用 Cron 表達式範例**

```java
// 每分鐘執行
@Scheduled(cron = "0 * * * * *")

// 每小時執行
@Scheduled(cron = "0 0 * * * *")

// 每天凌晨 2 點執行
@Scheduled(cron = "0 0 2 * * *")

// 每週一早上 9 點執行
@Scheduled(cron = "0 0 9 * * 1")

// 每月 1 號凌晨執行
@Scheduled(cron = "0 0 0 1 * *")

// 每 5 分鐘執行
@Scheduled(cron = "0 */5 * * * *")

// 每週一到週五的上午 9 點執行
@Scheduled(cron = "0 0 9 * * 1-5")

// 每天上午 9 點和下午 6 點執行
@Scheduled(cron = "0 0 9,18 * * *")
```

### **2. Fixed Rate 方式**

```java
// 每 60 秒執行一次（從上次開始時間計算）
@Scheduled(fixedRate = 60000)
public void taskWithFixedRate() {
    // Task implementation
}

// 帶初始延遲的固定頻率
@Scheduled(initialDelay = 30000, fixedRate = 60000)
public void taskWithInitialDelay() {
    // Task implementation
}
```

### **3. Fixed Delay 方式**

```java
// 上次執行完成後 60 秒再執行
@Scheduled(fixedDelay = 60000)
public void taskWithFixedDelay() {
    // Task implementation
}

// 帶初始延遲的固定延遲
@Scheduled(initialDelay = 30000, fixedDelay = 60000)
public void taskWithInitialDelayAndFixedDelay() {
    // Task implementation
}
```

---

## 🏗️ **實際應用範例**

### **完整的排程服務實作**

```java
@Slf4j
@Service
public class ScheduledNotificationService {
    
    private final BorrowRecordRepository borrowRecordRepository;
    
    public ScheduledNotificationService(BorrowRecordRepository borrowRecordRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
    }
    
    /**
     * Check for books due in 5 days and send notifications
     * Runs every minute for testing purposes
     * 
     * Cron expression: "0 * * * * *" = every minute
     * Format: second minute hour day month day-of-week
     */
    @Scheduled(cron = "0 * * * * *") // Every minute
    public void scheduledCheckOverdueNotifications() {
        checkOverdueNotifications();
    }
    
    /**
     * Check for books due in 5 days and send notifications
     * This method can be called manually for testing
     */
    public void checkOverdueNotifications() {
        log.info("=== Starting overdue notification check ===");
        
        try {
            // Calculate date range: 5 days from now
            LocalDateTime fiveDaysFromNow = LocalDateTime.now().plusDays(5);
            LocalDateTime sixDaysFromNow = LocalDateTime.now().plusDays(6);
            
            // Find all borrow records due in 5 days
            List<BorrowRecord> dueRecords = borrowRecordRepository.findByStatusAndDueAtBetween(
                "BORROWED", fiveDaysFromNow, sixDaysFromNow
            );
            
            if (dueRecords.isEmpty()) {
                log.info("No books due in 5 days");
                return;
            }
            
            log.info("Found {} books due in 5 days", dueRecords.size());
            
            // Send notifications for each due record
            for (BorrowRecord record : dueRecords) {
                sendOverdueNotification(record);
            }
            
            log.info("=== Overdue notification check completed ===");
            
        } catch (Exception e) {
            log.error("Error during overdue notification check", e);
        }
    }
    
    /**
     * Send overdue notification for a specific borrow record
     * 
     * @param record The borrow record to send notification for
     */
    private void sendOverdueNotification(BorrowRecord record) {
        try {
            // Simulate sending notification using System.out.println
            System.out.println("=== 借閱到期通知 ===");
            System.out.println("用戶: " + record.getUser().getName() + " (" + record.getUser().getEmail() + ")");
            System.out.println("書籍: " + record.getBookCopy().getBook().getTitle());
            System.out.println("副本編號: " + record.getBookCopy().getCopyNumber());
            System.out.println("借閱日期: " + record.getBorrowedAt());
            System.out.println("到期日期: " + record.getDueAt());
            System.out.println("圖書館: " + record.getBookCopy().getLibrary().getName());
            System.out.println("請在到期日前歸還書籍，避免逾期罰款。");
            System.out.println("==================");
            
            log.info("Sent overdue notification for user: {}, book: {}", 
                record.getUser().getName(), record.getBookCopy().getBook().getTitle());
                
        } catch (Exception e) {
            log.error("Error sending notification for record ID: {}", record.getId(), e);
        }
    }
}
```

### **手動觸發控制器**

```java
@Tag(name = "Notifications", description = "Notification management and testing endpoints")
@RestController
@RequestMapping("/notifications")
public class NotificationController {
    
    private final ScheduledNotificationService scheduledNotificationService;
    
    public NotificationController(ScheduledNotificationService scheduledNotificationService) {
        this.scheduledNotificationService = scheduledNotificationService;
    }
    
    /**
     * Manually trigger overdue notification check
     * This endpoint is for testing purposes only
     */
    @Operation(
        summary = "手動觸發逾期通知檢查",
        description = "手動執行逾期通知檢查，用於測試排程功能"
    )
    @PostMapping("/trigger-overdue-check")
    public ResponseEntity<ApiResponse<String>> triggerOverdueCheck() {
        try {
            // Manually trigger the scheduled task
            scheduledNotificationService.checkOverdueNotifications();
            
            return ResponseEntity.ok(ApiResponse.success("Overdue notification check triggered successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to trigger overdue notification check: " + e.getMessage()));
        }
    }
}
```

---

## 🧪 **測試和監控**

### **1. 查看執行日誌**

當排程任務執行時，你會在控制台看到類似這樣的日誌：

```
2025-08-23 15:30:00.123  INFO 12345 --- [scheduled-task-1] c.l.s.ScheduledNotificationService : === Starting overdue notification check ===
2025-08-23 15:30:00.456  INFO 12345 --- [scheduled-task-1] c.l.s.ScheduledNotificationService : No books due in 5 days
2025-08-23 15:30:00.789  INFO 12345 --- [scheduled-task-1] c.l.s.ScheduledNotificationService : === Overdue notification check completed ===
```

### **2. 手動觸發測試**

```bash
# 使用 curl 手動觸發
curl -X POST "http://localhost:8080/api/notifications/trigger-overdue-check"

# 使用 Swagger UI
# 訪問: http://localhost:8080/swagger-ui.html
# 找到 Notifications 區塊，測試 trigger-overdue-check 端點
```

### **3. 監控執行狀態**

```java
// 添加執行統計
@Slf4j
@Service
public class ScheduledNotificationService {
    
    private AtomicLong executionCount = new AtomicLong(0);
    private AtomicLong notificationCount = new AtomicLong(0);
    
    @Scheduled(cron = "0 * * * * *")
    public void scheduledCheckOverdueNotifications() {
        long startTime = System.currentTimeMillis();
        executionCount.incrementAndGet();
        
        try {
            checkOverdueNotifications();
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("Scheduled task completed in {}ms. Total executions: {}, Total notifications: {}", 
                duration, executionCount.get(), notificationCount.get());
        }
    }
}
```

---

## 🔒 **生產環境配置**

### **1. 環境特定的排程配置**

```java
@Configuration
@EnableScheduling
@Profile("!test") // 不在測試環境啟用
public class SchedulingConfig {
    
    @Bean
    @ConditionalOnProperty(name = "app.scheduling.enabled", havingValue = "true", matchIfMissing = true)
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        // Configuration
    }
}
```

### **2. 應用程式配置**

```yaml
# application.yml
app:
  scheduling:
    enabled: true
    pool-size: 5
    thread-name-prefix: "scheduled-task-"

# application-prod.yml
app:
  scheduling:
    enabled: true
    pool-size: 10
    thread-name-prefix: "prod-scheduled-"

# application-dev.yml
app:
  scheduling:
    enabled: true
    pool-size: 3
    thread-name-prefix: "dev-scheduled-"
```

### **3. 生產環境的 Cron 表達式**

```java
// 開發環境：每分鐘執行（方便測試）
@Profile("dev")
@Scheduled(cron = "0 * * * * *")
public void scheduledCheckOverdueNotificationsDev() {
    checkOverdueNotifications();
}

// 生產環境：每天上午 9 點執行
@Profile("prod")
@Scheduled(cron = "0 0 9 * * *")
public void scheduledCheckOverdueNotificationsProd() {
    checkOverdueNotifications();
}
```

---

## 📊 **最佳實踐**

### **1. 錯誤處理**

```java
@Scheduled(cron = "0 * * * * *")
public void scheduledTask() {
    try {
        // Task implementation
        performTask();
    } catch (Exception e) {
        log.error("Scheduled task failed", e);
        // 可以添加告警機制
        sendAlert("Scheduled task failed: " + e.getMessage());
    }
}
```

### **2. 避免長時間執行**

```java
@Scheduled(cron = "0 * * * * *")
public void scheduledTask() {
    long startTime = System.currentTimeMillis();
    long maxExecutionTime = 30000; // 30 seconds
    
    try {
        performTask();
    } finally {
        long duration = System.currentTimeMillis() - startTime;
        if (duration > maxExecutionTime) {
            log.warn("Scheduled task took too long: {}ms", duration);
        }
    }
}
```

### **3. 避免重複執行**

```java
@Slf4j
@Service
public class ScheduledNotificationService {
    
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    
    @Scheduled(cron = "0 * * * * *")
    public void scheduledCheckOverdueNotifications() {
        if (isRunning.compareAndSet(false, true)) {
            try {
                checkOverdueNotifications();
            } finally {
                isRunning.set(false);
            }
        } else {
            log.warn("Previous scheduled task is still running, skipping this execution");
        }
    }
}
```

### **4. 優雅的關閉**

```java
@Configuration
@EnableScheduling
public class SchedulingConfig {
    
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("scheduled-task-");
        
        // 優雅關閉配置
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        
        scheduler.initialize();
        return scheduler;
    }
}
```

---

## 🚀 **進階功能**

### **1. 動態排程**

```java
@Service
public class DynamicSchedulingService {
    
    private final ThreadPoolTaskScheduler scheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    
    public DynamicSchedulingService(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    public void scheduleTask(String taskId, String cronExpression, Runnable task) {
        // 取消現有任務
        cancelTask(taskId);
        
        // 建立新任務
        ScheduledFuture<?> future = scheduler.schedule(task, new CronTrigger(cronExpression));
        scheduledTasks.put(taskId, future);
    }
    
    public void cancelTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null) {
            future.cancel(false);
        }
    }
}
```

### **2. 集群環境處理**

```java
@Service
public class ClusterAwareScheduledService {
    
    @Value("${app.instance.id}")
    private String instanceId;
    
    @Scheduled(cron = "0 * * * * *")
    public void scheduledTask() {
        // 只有特定實例執行任務
        if (shouldExecuteTask()) {
            performTask();
        }
    }
    
    private boolean shouldExecuteTask() {
        // 根據實例 ID 或其他邏輯決定是否執行
        return instanceId.endsWith("0"); // 只有 ID 以 0 結尾的實例執行
    }
}
```

---

## 📚 **相關資源**

- [Spring Framework Scheduling Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#scheduling)
- [Cron Expression Generator](https://crontab.guru/)
- [Spring Boot Scheduling Guide](https://spring.io/guides/gs/scheduling-tasks/)

---

## 🎯 **總結**

### **✅ 已實作的功能**

1. **自動排程**: 每分鐘檢查逾期通知
2. **手動觸發**: 提供 API 端點供測試
3. **錯誤處理**: 完整的異常處理機制
4. **日誌記錄**: 詳細的執行日誌
5. **線程管理**: 自訂線程池配置

### **🔧 關鍵配置**

- `@EnableScheduling`: 啟用排程功能
- `@Scheduled(cron = "0 * * * * *")`: 設定執行頻率
- `ThreadPoolTaskScheduler`: 自訂線程池配置
- 錯誤處理和日誌記錄

### **📈 監控指標**

- 執行次數統計
- 執行時間監控
- 錯誤率追蹤
- 通知發送統計

**記住：好的排程系統是自動化業務流程的基礎！** 🎯
