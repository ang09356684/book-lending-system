package com.library.service;

import com.library.entity.BorrowRecord;
import com.library.entity.Notification;
import com.library.entity.User;
import com.library.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Notification Service - Handles scheduled notifications
 *
 * @author Library System
 * @version 1.0.0
 */
@Service
@Transactional
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final BorrowService borrowService;
    
    public NotificationService(NotificationRepository notificationRepository, BorrowService borrowService) {
        this.notificationRepository = notificationRepository;
        this.borrowService = borrowService;
    }
    
    /**
     * Create a new notification
     */
    public Notification createNotification(User user, String message, BorrowRecord borrowRecord) {
        Notification notification = new Notification(user, borrowRecord, message);
        return notificationRepository.save(notification);
    }
    
    /**
     * Send borrow confirmation notification
     */
    public Notification sendBorrowConfirmation(User user, BorrowRecord borrowRecord) {
        String message = String.format(
            "Book '%s' has been successfully borrowed. Due date: %s",
            borrowRecord.getBookCopy().getBook().getTitle(),
            borrowRecord.getDueAt().toLocalDate()
        );
        
        return createNotification(user, message, borrowRecord);
    }
    
    /**
     * Send overdue notification
     */
    public Notification sendOverdueNotification(User user, BorrowRecord borrowRecord) {
        String message = String.format(
            "Book '%s' is overdue. Please return it as soon as possible.",
            borrowRecord.getBookCopy().getBook().getTitle()
        );
        
        return createNotification(user, message, borrowRecord);
    }
    
    /**
     * Send return confirmation notification
     */
    public Notification sendReturnConfirmation(User user, BorrowRecord borrowRecord) {
        String message = String.format(
            "Book '%s' has been successfully returned.",
            borrowRecord.getBookCopy().getBook().getTitle()
        );
        
        return createNotification(user, message, borrowRecord);
    }
    
    /**
     * Get notifications for a user
     */
    public List<Notification> findByUser(User user) {
        return notificationRepository.findByUser(user);
    }
    
    /**
     * Delete notification
     */
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notificationRepository.delete(notification);
    }
    
    /**
     * Get notification by ID
     */
    public Notification findById(Long id) {
        return notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
    }
    
    /**
     * Send due date notifications every day at 9:00 AM
     * This scheduled task checks for books due in 5 days and sends notifications
     */
    @Scheduled(cron = "0 0 9 * * ?") // Every day at 9:00 AM
    public void sendDailyDueDateNotifications() {
        System.out.println("=== 開始執行每日到期通知檢查 ===");
        borrowService.sendDueDateNotifications();
        System.out.println("=== 每日到期通知檢查完成 ===");
    }
    
    /**
     * Manual method to trigger notifications (for testing purposes)
     */
    public void sendNotificationsNow() {
        System.out.println("=== 手動觸發到期通知檢查 ===");
        borrowService.sendDueDateNotifications();
        System.out.println("=== 手動觸發到期通知檢查完成 ===");
    }
}
