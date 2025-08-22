package com.library.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Borrow book request DTO
 * 
 * @author Library System
 * @version 1.0.0
 */
@Data
public class BorrowRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Library ID is required")
    private Long libraryId;
}
