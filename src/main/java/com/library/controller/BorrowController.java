package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.request.BorrowRequest;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;
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
    public ResponseEntity<ApiResponse<BorrowRecord>> borrowBook(
        @PathVariable Long bookCopyId,
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
    public ResponseEntity<ApiResponse<BorrowRecord>> returnBook(@PathVariable Long recordId) {
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
    public ResponseEntity<ApiResponse<List<BorrowRecord>>> getUserBorrows(@PathVariable Long userId) {
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
    public ResponseEntity<ApiResponse<List<BorrowRecord>>> getActiveBorrows(@PathVariable Long userId) {
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
    public ResponseEntity<ApiResponse<List<BorrowRecord>>> getOverdueRecords(@PathVariable Long userId) {
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
    public ResponseEntity<ApiResponse<BorrowRecord>> getBorrowRecord(@PathVariable Long id) {
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
    public ResponseEntity<ApiResponse<Boolean>> hasOverdueBooks(@PathVariable Long userId) {
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
    public ResponseEntity<ApiResponse<Long>> getActiveBorrowCount(@PathVariable Long userId) {
        long count = borrowService.countActiveBorrows(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
