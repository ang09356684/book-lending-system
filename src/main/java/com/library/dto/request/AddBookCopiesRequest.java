package com.library.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Add book copies request DTO
 * Add more copies to existing books across different libraries
 * 
 * @author Library System
 * @version 1.0.0
 */
@Data
public class AddBookCopiesRequest {
    
    @NotNull(message = "Book ID is required")
    private Long bookId;
    
    @NotEmpty(message = "At least one library copy configuration is required")
    @Valid
    private List<LibraryCopyConfig> libraryCopies;
    
    /**
     * Library copy configuration for a specific library
     */
    @Data
    public static class LibraryCopyConfig {
        
        @NotNull(message = "Library ID is required")
        private Long libraryId;
        
        @NotNull(message = "Number of copies is required")
        @jakarta.validation.constraints.Min(value = 1, message = "Number of copies must be at least 1")
        private Integer numberOfCopies;
        
        public LibraryCopyConfig() {
            this.numberOfCopies = 1; // Default to 1 copy
        }
    }
}
