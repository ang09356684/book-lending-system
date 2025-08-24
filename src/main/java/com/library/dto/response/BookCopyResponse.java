package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Book Copy Response DTO - Contains book copy information for borrowing
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCopyResponse {
    private Long copyId;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private Long libraryId;
    private String libraryName;
    private Integer copyNumber;
    private String status;
}
