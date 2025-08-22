package com.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Create book request DTO
 * 
 * @author Library System
 * @version 1.0.0
 */
@Data
public class CreateBookRequest {
    
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
    private String bookType = "圖書"; // Default to "圖書"
}
