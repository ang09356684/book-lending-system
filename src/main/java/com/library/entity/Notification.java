package com.library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Notification Entity - Represents notifications in the library system
 * 
 * @author Library System
 * @version 1.0.0
 */
@Entity
@Table(name = "notifications")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_record_id", nullable = false)
    private BorrowRecord borrowRecord;
    
    @Column(name = "message", nullable = false)
    private String message;
    
    @CreatedDate
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    // Constructor for basic notification creation
    public Notification(User user, BorrowRecord borrowRecord, String message) {
        this.user = user;
        this.borrowRecord = borrowRecord;
        this.message = message;
    }
}
