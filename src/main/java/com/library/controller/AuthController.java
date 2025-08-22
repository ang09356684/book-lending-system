package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.request.LibrarianRegisterRequest;
import com.library.dto.request.RegisterRequest;
import com.library.entity.User;
import com.library.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Authentication Controller - Handles user registration and authentication
 * 
 * @author Library System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Register a new user
     * 
     * @param request User registration request
     * @return Registered user information
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody @Valid RegisterRequest request) {
        User user = userService.registerUser(
            request.getUsername(),
            request.getEmail(),
            request.getPassword(),
            request.getFullName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(user, "User registered successfully"));
    }
    
    /**
     * Register a new librarian with external verification
     * 
     * @param request Librarian registration request
     * @return Registered librarian information
     */
    @PostMapping("/register/librarian")
    public ResponseEntity<ApiResponse<User>> registerLibrarian(@RequestBody @Valid LibrarianRegisterRequest request) {
        User librarian = userService.registerLibrarian(
            request.getUsername(),
            request.getEmail(),
            request.getPassword(),
            request.getFullName(),
            request.getLibrarianId()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(librarian, "Librarian registered successfully"));
    }
    
    /**
     * Check if username exists
     * 
     * @param username Username to check
     * @return Boolean indicating if username exists
     */
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
    
    /**
     * Check if email exists
     * 
     * @param email Email to check
     * @return Boolean indicating if email exists
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}
