package com.library.dto.request;

import com.library.constant.BookType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Update Book Request DTO - Contains book update information
 * 
 * @author Library System
 * @version 1.0.0
 */
@Data
public class UpdateBookRequest {
    
    @NotBlank(message = "Book title is required")
    @Size(min = 1, max = 200, message = "Book title length must be between 1-200 characters")
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(min = 1, max = 200, message = "Author length must be between 1-200 characters")
    private String author;
    
    @NotNull(message = "Published year is required")
    @Positive(message = "Published year must be positive")
    private Integer publishedYear;
    
    @NotBlank(message = "Category is required")
    @Size(min = 1, max = 50, message = "Category length must be between 1-50 characters")
    private String category;
    
    @NotBlank(message = "Book type is required")
    @Pattern(
        regexp = "^(圖書|書籍)$",
        message = "Book type must be either '" + BookType.TRADITIONAL + "' or '" + BookType.MODERN + "'"
    )
    private String bookType;
}
