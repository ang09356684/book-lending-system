package com.library.service;

import com.library.entity.BookCopy;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.repository.BookCopyRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Borrow Service - Business logic for borrowing management
 * 
 * @author Library System
 * @version 1.0.0
 */
@Service
@Transactional
public class BorrowService {
    
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;
    
    public BorrowService(BorrowRecordRepository borrowRecordRepository,
                        BookCopyRepository bookCopyRepository,
                        UserRepository userRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Borrow a book
     */
    public BorrowRecord borrowBook(Long userId, Long bookCopyId) {
        // Find user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Find book copy
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
            .orElseThrow(() -> new RuntimeException("Book copy not found"));
        
        // Check if book is available
        if (!"AVAILABLE".equals(bookCopy.getStatus())) {
            throw new RuntimeException("Book is not available");
        }
        
        // Check user borrowing limit (max 5 books)
        long activeBorrows = borrowRecordRepository.countByUserAndStatus(user, "BORROWED");
        if (activeBorrows >= 5) {
            throw new RuntimeException("Borrow limit exceeded. Maximum 5 books allowed.");
        }
        
        // Check for overdue books
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords(
            userId, "BORROWED", LocalDateTime.now()
        );
        if (!overdueRecords.isEmpty()) {
            throw new RuntimeException("User has overdue books. Please return them first.");
        }
        
        // Create borrow record
        BorrowRecord borrowRecord = new BorrowRecord(user, bookCopy, LocalDateTime.now().plusDays(30));
        
        // Update book copy status
        bookCopy.setStatus("BORROWED");
        bookCopyRepository.save(bookCopy);
        
        // Save borrow record
        return borrowRecordRepository.save(borrowRecord);
    }
    
    /**
     * Return a book
     */
    public BorrowRecord returnBook(Long borrowRecordId) {
        // Find borrow record
        BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowRecordId)
            .orElseThrow(() -> new RuntimeException("Borrow record not found"));
        
        // Check if already returned
        if ("RETURNED".equals(borrowRecord.getStatus())) {
            throw new RuntimeException("Book already returned");
        }
        
        // Update borrow record
        borrowRecord.setReturnedAt(LocalDateTime.now());
        borrowRecord.setStatus("RETURNED");
        
        // Update book copy status
        BookCopy bookCopy = borrowRecord.getBookCopy();
        bookCopy.setStatus("AVAILABLE");
        bookCopyRepository.save(bookCopy);
        
        // Save borrow record
        return borrowRecordRepository.save(borrowRecord);
    }
    
    /**
     * Get overdue records for a user
     */
    public List<BorrowRecord> getOverdueRecords(Long userId) {
        return borrowRecordRepository.findOverdueRecords(userId, "BORROWED", LocalDateTime.now());
    }
    
    /**
     * Get all borrow records for a user
     */
    public List<BorrowRecord> findByUser(User user) {
        return borrowRecordRepository.findByUser(user);
    }
    
    /**
     * Get active borrow records for a user
     */
    public List<BorrowRecord> getActiveBorrows(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return borrowRecordRepository.findByUserAndStatus(user, "BORROWED");
    }
    
    /**
     * Get borrow record by ID
     */
    public BorrowRecord findById(Long id) {
        return borrowRecordRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Borrow record not found"));
    }
    
    /**
     * Count active borrows for a user
     */
    public long countActiveBorrows(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return borrowRecordRepository.countByUserAndStatus(user, "BORROWED");
    }
    
    /**
     * Check if user has overdue books
     */
    public boolean hasOverdueBooks(Long userId) {
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords(
            userId, "BORROWED", LocalDateTime.now()
        );
        return !overdueRecords.isEmpty();
    }
}
