package com.library.service;

import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.RoleRepository;
import com.library.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, RoleRepository roleRepository, ExternalApiService externalApiService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.externalApiService = externalApiService;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Register a new user
     */
    public User registerUser(String name, String email, String password) {
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        
        if (password == null || password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }
        
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        // Get default role
        Role userRole = roleRepository.findByName("MEMBER")
            .orElseThrow(() -> new RuntimeException("Default role not found"));
        
        // Encrypt password
        String encodedPassword = passwordEncoder.encode(password);
        
        // Create user
        User user = new User(name, encodedPassword, email, userRole);
        return userRepository.save(user);
    }
    
    /**
     * Register a new librarian with external verification
     */
    public User registerLibrarian(String name, String email, String password, String librarianId) {
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Name is required");
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
        
        // Encrypt password
        String encodedPassword = passwordEncoder.encode(password);
        
        // Create librarian user
        User librarian = new User(name, encodedPassword, email, librarianRole);
        librarian.setLibrarianId(librarianId);
        librarian.setIsVerified(true);
        return userRepository.save(librarian);
    }
    
    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Find user by email with role loaded
     */
    public Optional<User> findByEmailWithRole(String email) {
        return userRepository.findByEmailWithRole(email);
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
     * Update user information
     */
    public User updateUser(Long userId, String name, String email, String password) {
        User user = findById(userId);
        
        // Update name if provided
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        }
        
        // Update email if provided and not already exists
        if (email != null && !email.trim().isEmpty()) {
            if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(email);
        }
        
        // Update password if provided
        if (password != null && !password.trim().isEmpty()) {
            if (password.length() < 6) {
                throw new RuntimeException("Password must be at least 6 characters");
            }
            user.setPassword(passwordEncoder.encode(password));
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Count users by role
     */
    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }
}
