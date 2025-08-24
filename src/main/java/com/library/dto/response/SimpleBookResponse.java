package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple Book Response DTO - Contains only essential book information
 * Used for list endpoints to avoid infinite recursion and excessive data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleBookResponse {
    private Long id;
    private String title;
    private String author;
    private Integer publishedYear;
    private String category;
    private String bookType;
}

