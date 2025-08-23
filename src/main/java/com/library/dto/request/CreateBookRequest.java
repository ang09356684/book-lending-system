package com.library.dto.request;

import com.library.constant.BookType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(
        regexp = "^(圖書|書籍)$",
        message = "Book type must be either '圖書' or '書籍'"
    )
    private String bookType = BookType.DEFAULT; // Default to "圖書"
}
