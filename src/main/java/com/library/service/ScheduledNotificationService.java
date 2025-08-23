package com.library.service;

import com.library.entity.BorrowRecord;
import com.library.repository.BorrowRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled notification service for overdue book reminders
 * 
 * @author Library System
 * @version 1.0.0
 */
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
    
    /**
     * Alternative method using fixed rate (every 60 seconds)
     * Uncomment this method and comment out the cron method above if you prefer fixed rate
     */
    /*
    @Scheduled(fixedRate = 60000) // Every 60 seconds
    public void checkOverdueNotificationsFixedRate() {
        checkOverdueNotifications();
    }
    */
    
    /**
     * Alternative method using fixed delay (60 seconds after previous execution completes)
     * Uncomment this method and comment out the cron method above if you prefer fixed delay
     */
    /*
    @Scheduled(fixedDelay = 60000) // 60 seconds after previous execution completes
    public void checkOverdueNotificationsFixedDelay() {
        checkOverdueNotifications();
    }
    */
}
