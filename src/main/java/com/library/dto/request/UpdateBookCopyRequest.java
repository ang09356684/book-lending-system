package com.library.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Update Book Copy Request DTO - Contains book copy update information
 * 
 * @author Library System
 * @version 1.0.0
 */
@Data
public class UpdateBookCopyRequest {
    
    @NotNull(message = "Copy number is required")
    @Positive(message = "Copy number must be positive")
    private Integer copyNumber;
    
    @NotNull(message = "Status is required")
    @Size(min = 1, max = 20, message = "Status length must be between 1-20 characters")
    private String status; // AVAILABLE, BORROWED, LOST, DAMAGED
}
