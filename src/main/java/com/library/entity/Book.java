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
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Association with BookCopy entity
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<BookCopy> bookCopies;
    
    // Constructor for basic book creation
    public Book(String title, String author, Integer publishedYear, String category) {
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
        this.category = category;
    }
}
