package com.library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * BorrowRecord Entity - Represents borrowing records in the library system
 * 
 * @author Library System
 * @version 1.0.0
 */
@Entity
@Table(name = "borrow_records")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;
    
    @CreatedDate
    @Column(name = "borrowed_at")
    private LocalDateTime borrowedAt;
    
    @Column(name = "due_at", nullable = false)
    private LocalDateTime dueAt;
    
    @Column(name = "returned_at")
    private LocalDateTime returnedAt;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "BORROWED";
    
    // Association with Notification entity
    @OneToMany(mappedBy = "borrowRecord", fetch = FetchType.LAZY)
    private List<Notification> notifications;
    
    // Constructor for basic borrow record creation
    public BorrowRecord(User user, BookCopy bookCopy, LocalDateTime dueAt) {
        this.user = user;
        this.bookCopy = bookCopy;
        this.dueAt = dueAt;
    }
}
