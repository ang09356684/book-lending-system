package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Book with Copy Summary Response DTO - Contains book information and copy statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookWithCopySummaryResponse {
    private Long id;
    private String title;
    private String author;
    private Integer publishedYear;
    private String category;
    private String bookType;
    private CopySummary copySummary;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CopySummary {
        private int totalCopies;
        private int availableCopies;
        private List<LibraryCopySummary> libraryCopies;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LibraryCopySummary {
        private Long libraryId;
        private String libraryName;
        private int totalCopies;
        private int availableCopies;
    }
}
