package com.library.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Update User Request DTO - Contains user update information
 * 
 * @author Library System
 * @version 1.0.0
 */
@Data
public class UpdateUserRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name length must be between 2-100 characters")
    private String name;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password; // Optional for update
}
