package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.request.BorrowRequest;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/borrows")
@Validated
@Tag(name = "Borrows", description = "Book borrowing and returning endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class BorrowController {
    
    private final BorrowService borrowService;
    
    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }
    
    /**
     * Borrow a book
     * 
     * @param bookCopyId Book copy ID
     * @param request Borrow request
     * @return Borrow record
     */
    @PostMapping("/{bookCopyId}/borrow")
    @Operation(
        summary = "Borrow a book",
        description = "Borrow a book copy for a user"
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
            description = "Book copy or user not found"
        )
    })
    public ResponseEntity<ApiResponse<BorrowRecord>> borrowBook(
        @Parameter(description = "Book copy ID", required = true, example = "1")
        @PathVariable Long bookCopyId,
        @Parameter(description = "Borrow request information", required = true)
        @RequestBody @Valid BorrowRequest request
    ) {
        BorrowRecord record = borrowService.borrowBook(
            request.getUserId(),
            bookCopyId
        );
        
        return ResponseEntity.ok(ApiResponse.success(record, "Book borrowed successfully"));
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
    public ResponseEntity<ApiResponse<BorrowRecord>> returnBook(
        @Parameter(description = "Borrow record ID", required = true, example = "1")
        @PathVariable Long recordId
    ) {
        BorrowRecord record = borrowService.returnBook(recordId);
        return ResponseEntity.ok(ApiResponse.success(record, "Book returned successfully"));
    }
    
    /**
     * Get borrow records for a user
     * 
     * @param userId User ID
     * @return List of borrow records
     */
    @GetMapping("/user/{userId}")
    @Operation(
        summary = "Get user borrow records",
        description = "Get all borrow records for a specific user"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Borrow records retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<BorrowRecord>>> getUserBorrows(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable Long userId
    ) {
        // Note: This method requires UserService to get User by ID
        // For now, we'll use getActiveBorrows which handles the User lookup internally
        List<BorrowRecord> records = borrowService.getActiveBorrows(userId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
    
    /**
     * Get active borrows for a user
     * 
     * @param userId User ID
     * @return List of active borrow records
     */
    @GetMapping("/user/{userId}/active")
    @Operation(
        summary = "Get active borrows",
        description = "Get all currently active borrow records for a user"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Active borrows retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<BorrowRecord>>> getActiveBorrows(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable Long userId
    ) {
        List<BorrowRecord> records = borrowService.getActiveBorrows(userId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
    
    /**
     * Get overdue records for a user
     * 
     * @param userId User ID
     * @return List of overdue borrow records
     */
    @GetMapping("/user/{userId}/overdue")
    @Operation(
        summary = "Get overdue records",
        description = "Get all overdue borrow records for a user"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Overdue records retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<BorrowRecord>>> getOverdueRecords(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable Long userId
    ) {
        List<BorrowRecord> records = borrowService.getOverdueRecords(userId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
    
    /**
     * Get borrow record by ID
     * 
     * @param id Borrow record ID
     * @return Borrow record
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get borrow record by ID",
        description = "Get a specific borrow record by its ID"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Borrow record found successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Borrow record not found"
        )
    })
    public ResponseEntity<ApiResponse<BorrowRecord>> getBorrowRecord(
        @Parameter(description = "Borrow record ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        BorrowRecord record = borrowService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(record));
    }
    
    /**
     * Check if user has overdue books
     * 
     * @param userId User ID
     * @return Boolean indicating if user has overdue books
     */
    @GetMapping("/user/{userId}/has-overdue")
    @Operation(
        summary = "Check overdue status",
        description = "Check if a user has any overdue books"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Overdue status checked successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "data": false,
                        "message": "User has no overdue books"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Boolean>> hasOverdueBooks(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable Long userId
    ) {
        boolean hasOverdue = borrowService.hasOverdueBooks(userId);
        return ResponseEntity.ok(ApiResponse.success(hasOverdue));
    }
    
    /**
     * Get active borrow count for a user
     * 
     * @param userId User ID
     * @return Number of active borrows
     */
    @GetMapping("/user/{userId}/active-count")
    @Operation(
        summary = "Get active borrow count",
        description = "Get the number of currently active borrows for a user"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Active borrow count retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<Long>> getActiveBorrowCount(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable Long userId
    ) {
        long count = borrowService.countActiveBorrows(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
