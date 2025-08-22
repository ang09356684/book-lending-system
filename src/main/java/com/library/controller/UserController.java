package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.entity.User;
import com.library.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Controller - Handles user management operations
 * 
 * @author Library System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Get user by ID
     * 
     * @param id User ID
     * @return User information
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    /**
     * Get user by username
     * 
     * @param username Username
     * @return User information
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<User>> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    /**
     * Get user by email
     * 
     * @param email Email
     * @return User information
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<User>> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    /**
     * Get all users by role
     * 
     * @param roleName Role name (USER, LIBRARIAN, ADMIN)
     * @return List of users with the specified role
     */
    @GetMapping("/role/{roleName}")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByRole(@PathVariable String roleName) {
        // This would require RoleService to get Role by name
        // For now, we'll return an empty list as placeholder
        return ResponseEntity.ok(ApiResponse.success(List.of()));
    }
    
    /**
     * Get verified users by role
     * 
     * @param roleName Role name (USER, LIBRARIAN, ADMIN)
     * @return List of verified users with the specified role
     */
    @GetMapping("/role/{roleName}/verified")
    public ResponseEntity<ApiResponse<List<User>>> getVerifiedUsersByRole(@PathVariable String roleName) {
        // This would require RoleService to get Role by name
        // For now, we'll return an empty list as placeholder
        return ResponseEntity.ok(ApiResponse.success(List.of()));
    }
    
    /**
     * Update user verification status
     * 
     * @param id User ID
     * @param verified Verification status
     * @return Updated user information
     */
    @PutMapping("/{id}/verification")
    public ResponseEntity<ApiResponse<User>> updateVerificationStatus(
        @PathVariable Long id,
        @RequestParam boolean verified
    ) {
        User user = userService.updateVerificationStatus(id, verified);
        return ResponseEntity.ok(ApiResponse.success(user, "Verification status updated successfully"));
    }
    
    /**
     * Count users by role
     * 
     * @param roleName Role name (USER, LIBRARIAN, ADMIN)
     * @return Number of users with the specified role
     */
    @GetMapping("/role/{roleName}/count")
    public ResponseEntity<ApiResponse<Long>> countUsersByRole(@PathVariable String roleName) {
        // This would require RoleService to get Role by name
        // For now, we'll return 0 as placeholder
        return ResponseEntity.ok(ApiResponse.success(0L));
    }
}
