package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.request.BorrowRequest;
import com.library.dto.response.BorrowRecordResponse;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.service.BorrowService;
import com.library.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Borrow Controller - Handles book borrowing and returning operations
 * 
 * @author Library System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/borrows")
@Validated
@Tag(name = "Borrows", description = "Book borrowing and returning endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class BorrowController {
    
    private final BorrowService borrowService;
    private final UserService userService;
    
    public BorrowController(BorrowService borrowService, UserService userService) {
        this.borrowService = borrowService;
        this.userService = userService;
    }
    
    /**
     * Borrow a book
     * 
     * @param request Borrow request (only bookCopyId needed)
     * @param authentication Current user authentication
     * @return Borrow record
     */
    @PostMapping
    @Operation(
        summary = "Borrow a book",
        description = "Borrow a book copy for the current authenticated user"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Book borrowed successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "message": "Book borrowed successfully",
                        "data": {
                            "id": 1,
                            "borrowDate": "2024-01-15T10:00:00",
                            "dueDate": "2024-02-15T10:00:00",
                            "returnDate": null,
                            "status": "BORROWED"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Book not available or user has overdue books"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Book copy not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "User not authenticated"
        )
    })
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> borrowBook(
        @Parameter(description = "Borrow request information (only bookCopyId required)", required = true)
        @RequestBody @Valid BorrowRequest request,
        Authentication authentication
    ) {
        // Get current user from JWT token
        String currentUserEmail = authentication.getName();
        User currentUser = userService.findByEmail(currentUserEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        BorrowRecord record = borrowService.borrowBook(
            currentUser.getId(),  // Use current user ID instead of userId from request
            request.getBookCopyId()
        );
        
        BorrowRecordResponse response = convertToBorrowRecordResponse(record);
        return ResponseEntity.ok(ApiResponse.success(response, "Book borrowed successfully"));
    }
    
    /**
     * Return a book
     * 
     * @param recordId Borrow record ID
     * @return Updated borrow record
     */
    @PostMapping("/{recordId}/return")
    @Operation(
        summary = "Return a book",
        description = "Return a borrowed book"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Book returned successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Borrow record not found"
        )
    })
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> returnBook(
        @Parameter(description = "Borrow record ID", required = true, example = "1")
        @PathVariable Long recordId
    ) {
        BorrowRecord record = borrowService.returnBook(recordId);
        BorrowRecordResponse response = convertToBorrowRecordResponse(record);
        return ResponseEntity.ok(ApiResponse.success(response, "Book returned successfully"));
    }
    

    

    

    
    /**
     * Get borrow records for the current authenticated user
     * 
     * @param authentication Current user authentication
     * @return List of borrow records
     */
    @GetMapping
    @Operation(
        summary = "Get borrow records",
        description = "Get borrow records for the current authenticated user"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Borrow records retrieved successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "User not authenticated"
        )
    })
    public ResponseEntity<ApiResponse<List<BorrowRecordResponse>>> getBorrowRecords(
        Authentication authentication
    ) {
        // Get current user from JWT token
        String currentUserEmail = authentication.getName();
        User currentUser = userService.findByEmail(currentUserEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<BorrowRecord> records = borrowService.getActiveBorrows(currentUser.getId());
        
        List<BorrowRecordResponse> responses = records.stream()
            .map(this::convertToBorrowRecordResponse)
            .toList();
            
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
    

    

    

    
    /**
     * Convert BorrowRecord entity to BorrowRecordResponse DTO
     */
    private BorrowRecordResponse convertToBorrowRecordResponse(BorrowRecord record) {
        return new BorrowRecordResponse(
            record.getId(),
            record.getUser().getName(),
            record.getBookCopy().getBook().getTitle(),
            record.getBookCopy().getLibrary().getName(),
            record.getBookCopy().getCopyNumber(),
            record.getBorrowedAt(),
            record.getDueAt(),
            record.getReturnedAt(),
            record.getStatus()
        );
    }
}
