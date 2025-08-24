package com.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Update Library Request DTO - Contains library update information
 * 
 * @author Library System
 * @version 1.0.0
 */
@Data
public class UpdateLibraryRequest {
    
    @NotBlank(message = "Library name is required")
    @Size(min = 2, max = 100, message = "Library name length must be between 2-100 characters")
    private String name;
    
    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 500, message = "Address length must be between 5-500 characters")
    private String address;
    
    @Pattern(
        regexp = "^[+]?[0-9\\s\\-\\(\\)]{7,20}$", 
        message = "Invalid phone number format"
    )
    @Size(max = 20, message = "Phone number length cannot exceed 20 characters")
    private String phone;
}
