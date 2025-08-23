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
        
        // Get book type and check borrowing limits
        String bookType = bookCopy.getBook().getBookType();
        checkBorrowingLimits(user, bookType);
        
        // Check for overdue books
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords(
            userId, "BORROWED", LocalDateTime.now()
        );
        if (!overdueRecords.isEmpty()) {
            throw new RuntimeException("User has overdue books. Please return them first.");
        }
        
        // Create borrow record with 30 days loan period
        BorrowRecord borrowRecord = new BorrowRecord(user, bookCopy, LocalDateTime.now().plusDays(30));
        
        // Update book copy status
        bookCopy.setStatus("BORROWED");
        bookCopyRepository.save(bookCopy);
        
        // Save borrow record
        return borrowRecordRepository.save(borrowRecord);
    }
    
    /**
     * Check borrowing limits based on book type
     * 圖書: maximum 5 books
     * 書籍: maximum 10 books
     */
    private void checkBorrowingLimits(User user, String bookType) {
        List<BorrowRecord> activeBorrows = borrowRecordRepository.findByUserAndStatus(user, "BORROWED");
        
        if ("圖書".equals(bookType)) {
            // Check if user already has 5 圖書
            long bookCount = activeBorrows.stream()
                .filter(record -> "圖書".equals(record.getBookCopy().getBook().getBookType()))
                .count();
            
            if (bookCount >= 5) {
                throw new RuntimeException("圖書借閱限制已達上限 (最多5本)");
            }
        } else if ("書籍".equals(bookType)) {
            // Check if user already has 10 書籍
            long bookCount = activeBorrows.stream()
                .filter(record -> "書籍".equals(record.getBookCopy().getBook().getBookType()))
                .count();
            
            if (bookCount >= 10) {
                throw new RuntimeException("書籍借閱限制已達上限 (最多10本)");
            }
        }
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
     * Send notifications for books due in 5 days
     * This method should be called by a scheduled task
     */
    public void sendDueDateNotifications() {
        LocalDateTime fiveDaysFromNow = LocalDateTime.now().plusDays(5);
        LocalDateTime sixDaysFromNow = LocalDateTime.now().plusDays(6);
        
        // Find all borrow records due in 5 days
        List<BorrowRecord> dueRecords = borrowRecordRepository.findByStatusAndDueAtBetween(
            "BORROWED", fiveDaysFromNow, sixDaysFromNow
        );
        
        for (BorrowRecord record : dueRecords) {
            // Simulate sending notification using System.out.println
            System.out.println("=== 借閱到期通知 ===");
            System.out.println("用戶: " + record.getUser().getName() + " (" + record.getUser().getEmail() + ")");
            System.out.println("書籍: " + record.getBookCopy().getBook().getTitle());
            System.out.println("到期日期: " + record.getDueAt());
            System.out.println("請在到期日前歸還書籍，避免逾期罰款。");
            System.out.println("==================");
        }
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
    
    /**
     * Get borrowing statistics for a user
     */
    public BorrowingStats getBorrowingStats(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<BorrowRecord> activeBorrows = borrowRecordRepository.findByUserAndStatus(user, "BORROWED");
        
        long bookCount = activeBorrows.stream()
            .filter(record -> "圖書".equals(record.getBookCopy().getBook().getBookType()))
            .count();
            
        long bookCount2 = activeBorrows.stream()
            .filter(record -> "書籍".equals(record.getBookCopy().getBook().getBookType()))
            .count();
        
        return new BorrowingStats(bookCount, bookCount2);
    }
    
    /**
     * Borrowing statistics for a user
     */
    public static class BorrowingStats {
        private final long bookCount; // 圖書 count
        private final long bookCount2; // 書籍 count
        
        public BorrowingStats(long bookCount, long bookCount2) {
            this.bookCount = bookCount;
            this.bookCount2 = bookCount2;
        }
        
        public long getBookCount() { return bookCount; }
        public long getBookCount2() { return bookCount2; }
        public long getTotalCount() { return bookCount + bookCount2; }
    }
}
