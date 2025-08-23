package com.library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Book Entity - Represents books in the library system
 * 
 * @author Library System
 * @version 1.0.0
 */
@Entity
@Table(name = "books")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "author", nullable = false, length = 200)
    private String author;
    
    @Column(name = "published_year")
    private Integer publishedYear;
    
    @Column(name = "category", nullable = false, length = 50)
    private String category;
    
    @Column(name = "book_type", nullable = false, length = 20)
    private String bookType; // "圖書" or "書籍"
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Association with BookCopy entity
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<BookCopy> bookCopies;
    
    // Constructor for basic book creation
    public Book(String title, String author, Integer publishedYear, String category, String bookType) {
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
        this.category = category;
        this.bookType = bookType;
    }
    
    // Constructor for backward compatibility
    public Book(String title, String author, Integer publishedYear, String category) {
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
        this.category = category;
        this.bookType = "圖書"; // Default to "圖書"
    }
}
