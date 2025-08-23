package com.library.repository;

import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BookCopy Repository - Data access layer for BookCopy entity
 * 
 * @author Library System
 * @version 1.0.0
 */
@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    
    // Basic query methods
    List<BookCopy> findByBook(Book book);
    List<BookCopy> findByLibrary(Library library);
    List<BookCopy> findByStatus(String status);
    
    // Availability queries
    List<BookCopy> findByBookAndStatus(Book book, String status);
    List<BookCopy> findByLibraryAndStatus(Library library, String status);
    
    // Complex queries
    @Query("SELECT bc FROM BookCopy bc WHERE " +
           "bc.book.id = :bookId AND " +
           "bc.library.id = :libraryId AND " +
           "bc.status = :status")
    List<BookCopy> findAvailableCopies(@Param("bookId") Long bookId, 
                                       @Param("libraryId") Long libraryId, 
                                       @Param("status") String status);
    
    // Statistics queries
    long countByBookAndStatus(Book book, String status);
    long countByLibraryAndStatus(Library library, String status);
    long countByBook(Book book);
    long countByLibrary(Library library);
    
    // Find copies by book and library
    @Query("SELECT bc FROM BookCopy bc WHERE bc.book.id = :bookId AND bc.library.id = :libraryId")
    List<BookCopy> findByBookIdAndLibraryId(@Param("bookId") Long bookId, @Param("libraryId") Long libraryId);
}
