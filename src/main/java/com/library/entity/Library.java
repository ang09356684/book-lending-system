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
 * Library Entity - Represents libraries in the library system
 * 
 * @author Library System
 * @version 1.0.0
 */
@Entity
@Table(name = "libraries")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Library {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;
    
    @Column(name = "address", nullable = false)
    private String address;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Association with BookCopy entity
    @OneToMany(mappedBy = "library", fetch = FetchType.LAZY)
    private List<BookCopy> bookCopies;
    
    // Constructor for basic library creation
    public Library(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }
}
