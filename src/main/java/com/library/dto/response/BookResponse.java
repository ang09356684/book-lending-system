package com.library.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Book Response DTO - Contains only necessary book information for API responses
 * 
 * @author Library System
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
public class BookResponse {
    
    private Long id;
    private String title;
    private String author;
    private Integer publishedYear;
    private String category;
    private String bookType;
    
    public BookResponse(Long id, String title, String author, Integer publishedYear, String category, String bookType) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
        this.category = category;
        this.bookType = bookType;
    }
}
