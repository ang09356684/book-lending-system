package com.library.service;

import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.entity.Library;
import com.library.repository.BookCopyRepository;
import com.library.repository.BookRepository;
import com.library.repository.LibraryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Book Service - Business logic for book management
 * 
 * @author Library System
 * @version 1.0.0
 */
@Service
@Transactional
public class BookService {
    
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final LibraryRepository libraryRepository;
    
    public BookService(BookRepository bookRepository, 
                      BookCopyRepository bookCopyRepository,
                      LibraryRepository libraryRepository) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.libraryRepository = libraryRepository;
    }
    
    /**
     * Create a new book
     */
    public Book createBook(String title, String author, Integer publishedYear, String category) {
        // Validate input
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("Book title is required");
        }
        
        if (author == null || author.trim().isEmpty()) {
            throw new RuntimeException("Book author is required");
        }
        
        if (category == null || category.trim().isEmpty()) {
            throw new RuntimeException("Book category is required");
        }
        
        // Create book
        Book book = new Book(title, author, publishedYear, category);
        return bookRepository.save(book);
    }
    
    /**
     * Search books
     */
    public List<Book> searchBooks(String title, String author, String category) {
        return bookRepository.searchBooks(title, author, category);
    }
    
    /**
     * Find books by category
     */
    public List<Book> findByCategory(String category) {
        return bookRepository.findByCategory(category);
    }
    
    /**
     * Find books by author
     */
    public List<Book> findByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }
    
    /**
     * Find books by published year
     */
    public List<Book> findByPublishedYear(Integer publishedYear) {
        return bookRepository.findByPublishedYear(publishedYear);
    }
    
    /**
     * Get book by ID
     */
    public Book findById(Long id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));
    }
    
    /**
     * Count books by category
     */
    public long countByCategory(String category) {
        return bookRepository.countByCategory(category);
    }
    
    /**
     * Count books by author
     */
    public long countByAuthor(String author) {
        return bookRepository.countByAuthor(author);
    }
    
    /**
     * Count books by published year
     */
    public long countByPublishedYear(Integer publishedYear) {
        return bookRepository.countByPublishedYear(publishedYear);
    }
    
    /**
     * Check book availability
     */
    public boolean isBookAvailable(Long bookId, Long libraryId) {
        // Validate library exists
        libraryRepository.findById(libraryId)
            .orElseThrow(() -> new RuntimeException("Library not found"));
            
        List<BookCopy> availableCopies = bookCopyRepository.findAvailableCopies(bookId, libraryId, "AVAILABLE");
        return !availableCopies.isEmpty();
    }
    
    /**
     * Get available copy count for a book
     */
    public long getAvailableCopyCount(Long bookId, Long libraryId) {
        // Validate library exists
        libraryRepository.findById(libraryId)
            .orElseThrow(() -> new RuntimeException("Library not found"));
            
        return bookCopyRepository.countByBookAndStatus(
            findById(bookId), "AVAILABLE"
        );
    }
    
    /**
     * Get total copy count for a book
     */
    public long getTotalCopyCount(Long bookId) {
        return bookCopyRepository.countByBook(findById(bookId));
    }
}
