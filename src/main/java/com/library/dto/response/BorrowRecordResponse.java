package com.library.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * BorrowRecord Response DTO - Contains only necessary borrow record information for API responses
 * 
 * @author Library System
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
public class BorrowRecordResponse {
    
    private Long id;
    private String userName;
    private String bookTitle;
    private String libraryName;
    private Integer copyNumber;
    private LocalDateTime borrowedAt;
    private LocalDateTime dueAt;
    private LocalDateTime returnedAt;
    private String status;
    
    public BorrowRecordResponse(Long id, String userName, String bookTitle, String libraryName, 
                               Integer copyNumber, LocalDateTime borrowedAt, LocalDateTime dueAt, 
                               LocalDateTime returnedAt, String status) {
        this.id = id;
        this.userName = userName;
        this.bookTitle = bookTitle;
        this.libraryName = libraryName;
        this.copyNumber = copyNumber;
        this.borrowedAt = borrowedAt;
        this.dueAt = dueAt;
        this.returnedAt = returnedAt;
        this.status = status;
    }
}
