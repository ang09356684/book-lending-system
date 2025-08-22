package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Role Entity - Represents user roles in the library system
 * 
 * @author Library System
 * @version 1.0.0
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    // Association with User entity
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<User> users;
    
    // Constructor for basic role creation
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
