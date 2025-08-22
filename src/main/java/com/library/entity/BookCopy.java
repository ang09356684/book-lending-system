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
 * BookCopy Entity - Represents book copies in the library system
 * 
 * @author Library System
 * @version 1.0.0
 */
@Entity
@Table(name = "book_copies")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCopy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id", nullable = false)
    private Library library;
    
    @Column(name = "copy_number", nullable = false)
    private Integer copyNumber;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "AVAILABLE";
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Association with BorrowRecord entity
    @OneToMany(mappedBy = "bookCopy", fetch = FetchType.LAZY)
    private List<BorrowRecord> borrowRecords;
    
    // Constructor for basic book copy creation
    public BookCopy(Book book, Library library, Integer copyNumber) {
        this.book = book;
        this.library = library;
        this.copyNumber = copyNumber;
    }
}
