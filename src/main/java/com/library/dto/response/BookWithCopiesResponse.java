package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Book with copies response DTO
 * 
 * @author Library System
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookWithCopiesResponse {
    
    private Long id;
    private String title;
    private String author;
    private Integer publishedYear;
    private String category;
    private String bookType;
    private List<LibraryCopyInfo> libraryCopies;
    
    /**
     * Library copy information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LibraryCopyInfo {
        private Long libraryId;
        private String libraryName;
        private Integer numberOfCopies;
        private List<CopyInfo> copies;
    }
    
    /**
     * Individual copy information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CopyInfo {
        private Long copyId;
        private Integer copyNumber;
        private String status;
    }
}
