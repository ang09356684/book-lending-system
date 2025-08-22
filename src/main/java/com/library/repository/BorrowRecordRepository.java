package com.library.repository;

import com.library.entity.BookCopy;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * BorrowRecord Repository - Data access layer for BorrowRecord entity
 * 
 * @author Library System
 * @version 1.0.0
 */
@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    
    // Basic query methods
    List<BorrowRecord> findByUser(User user);
    List<BorrowRecord> findByBookCopy(BookCopy bookCopy);
    List<BorrowRecord> findByStatus(String status);
    
    // Active borrowing queries
    List<BorrowRecord> findByUserAndStatus(User user, String status);
    
    // Overdue queries
    @Query("SELECT br FROM BorrowRecord br WHERE " +
           "br.user.id = :userId AND " +
           "br.status = :status AND " +
           "br.dueAt < :currentDate")
    List<BorrowRecord> findOverdueRecords(@Param("userId") Long userId, 
                                          @Param("status") String status, 
                                          @Param("currentDate") LocalDateTime currentDate);
    
    // Notification queries - find records due in specific date range
    @Query("SELECT br FROM BorrowRecord br WHERE " +
           "br.status = :status AND " +
           "br.dueAt >= :startDate AND " +
           "br.dueAt < :endDate")
    List<BorrowRecord> findByStatusAndDueAtBetween(@Param("status") String status,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);
    
    // Pagination queries
    Page<BorrowRecord> findByUser(User user, Pageable pageable);
    Page<BorrowRecord> findByUserAndStatus(User user, String status, Pageable pageable);
    
    // Statistics queries
    long countByUserAndStatus(User user, String status);
    long countByBookCopy(BookCopy bookCopy);
    long countByStatus(String status);
}
