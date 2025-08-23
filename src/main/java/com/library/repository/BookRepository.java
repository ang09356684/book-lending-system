package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Book Repository - Data access layer for Book entity
 * 
 * @author Library System
 * @version 1.0.0
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Basic query methods
    List<Book> findByAuthor(String author);
    List<Book> findByCategory(String category);
    List<Book> findByPublishedYear(Integer publishedYear);
    
    // Search functionality
    @Query(value = "SELECT * FROM books WHERE " +
           "(:title IS NULL OR title ILIKE '%' || :title || '%') AND " +
           "(:author IS NULL OR author ILIKE '%' || :author || '%') AND " +
           "(:category IS NULL OR category = :category)", 
           nativeQuery = true)
    List<Book> searchBooks(@Param("title") String title, 
                           @Param("author") String author, 
                           @Param("category") String category);
    
    // Pagination queries
    Page<Book> findByCategory(String category, Pageable pageable);
    Page<Book> findByAuthor(String author, Pageable pageable);
    
    // Statistics queries
    long countByCategory(String category);
    long countByAuthor(String author);
    long countByPublishedYear(Integer publishedYear);
}
