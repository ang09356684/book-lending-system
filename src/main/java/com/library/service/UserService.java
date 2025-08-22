package com.library.service;

import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.RoleRepository;
import com.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * User Service - Business logic for user management
 * 
 * @author Library System
 * @version 1.0.0
 */
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ExternalApiService externalApiService;
    
    public UserService(UserRepository userRepository, RoleRepository roleRepository, ExternalApiService externalApiService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.externalApiService = externalApiService;
    }
    
    /**
     * Register a new user
     */
    public User registerUser(String username, String email, String password, String fullName) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        
        if (password == null || password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }
        
        // Check if user already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        // Get default role
        Role userRole = roleRepository.findByName("USER")
            .orElseThrow(() -> new RuntimeException("Default role not found"));
        
        // Create user
        User user = new User(username, password, email, fullName, userRole);
        return userRepository.save(user);
    }
    
    /**
     * Register a new librarian with external verification
     */
    public User registerLibrarian(String username, String email, String password, String fullName, String librarianId) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        
        if (password == null || password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }
        
        if (librarianId == null || librarianId.trim().isEmpty()) {
            throw new RuntimeException("Librarian ID is required");
        }
        
        // Check if user already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        // Verify librarian with external system
        boolean isVerified = externalApiService.verifyLibrarian(librarianId);
        if (!isVerified) {
            throw new RuntimeException("Librarian verification failed. Please check your librarian ID.");
        }
        
        // Get librarian role
        Role librarianRole = roleRepository.findByName("LIBRARIAN")
            .orElseThrow(() -> new RuntimeException("Librarian role not found"));
        
        // Create librarian user
        User librarian = new User(username, password, email, fullName, librarianRole);
        return userRepository.save(librarian);
    }
    
    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Find users by role
     */
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Find verified users by role
     */
    public List<User> findVerifiedUsersByRole(Role role) {
        return userRepository.findVerifiedUsersByRole(role, true);
    }
    
    /**
     * Check if username exists
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Get user by ID
     */
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    /**
     * Update user verification status
     */
    public User updateVerificationStatus(Long userId, boolean isVerified) {
        User user = findById(userId);
        user.setIsVerified(isVerified);
        return userRepository.save(user);
    }
    
    /**
     * Count users by role
     */
    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }
}
