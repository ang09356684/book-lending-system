package com.library.repository;

import com.library.entity.BorrowRecord;
import com.library.entity.Notification;
import com.library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Notification Repository - Data access layer for Notification entity
 * 
 * @author Library System
 * @version 1.0.0
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Basic query methods
    List<Notification> findByUser(User user);
    List<Notification> findByBorrowRecord(BorrowRecord borrowRecord);
    
    // Pagination queries
    Page<Notification> findByUser(User user, Pageable pageable);
    
    // Statistics queries
    long countByUser(User user);
    long countByBorrowRecord(BorrowRecord borrowRecord);
}
