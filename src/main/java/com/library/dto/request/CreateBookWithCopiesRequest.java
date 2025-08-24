package com.library.dto.request;

import com.library.constant.BookType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Create book with copies request DTO
 * Supports multi-library collections and multiple copies per library
 * 
 * @author Library System
 * @version 1.0.0
 */
@Data
public class CreateBookWithCopiesRequest {
    
    @NotBlank(message = "Book title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @NotBlank(message = "Book author is required")
    @Size(max = 100, message = "Author must not exceed 100 characters")
    private String author;
    
    private Integer publishedYear;
    
    @NotBlank(message = "Book category is required")
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;
    
    @NotBlank(message = "Book type is required")
    @Size(max = 20, message = "Book type must not exceed 20 characters")
    private String bookType = BookType.DEFAULT; // Default to "圖書"
    
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
