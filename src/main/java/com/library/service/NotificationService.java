package com.library.service;

import com.library.entity.BorrowRecord;
import com.library.entity.Notification;
import com.library.entity.User;
import com.library.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
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
    

}
