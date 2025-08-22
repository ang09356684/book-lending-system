package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.request.CreateBookRequest;
import com.library.entity.Book;
import com.library.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Book Controller - Handles book management operations
 * 
 * @author Library System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {
    
    private final BookService bookService;
    
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    /**
     * Get all books with pagination
     * 
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @return List of books
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Book>>> getAllBooks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        List<Book> books = bookService.searchBooks(null, null, null);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    /**
     * Get book by ID
     * 
     * @param id Book ID
     * @return Book information
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> getBook(@PathVariable Long id) {
        Book book = bookService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(book));
    }
    
    /**
     * Create a new book
     * 
     * @param request Book creation request
     * @return Created book information
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Book>> createBook(@RequestBody @Valid CreateBookRequest request) {
        Book book = bookService.createBook(
            request.getTitle(),
            request.getAuthor(),
            request.getPublishedYear(),
            request.getCategory()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(book, "Book created successfully"));
    }
    
    /**
     * Search books by criteria
     * 
     * @param title Book title (optional)
     * @param author Book author (optional)
     * @param category Book category (optional)
     * @return List of matching books
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Book>>> searchBooks(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String author,
        @RequestParam(required = false) String category
    ) {
        List<Book> books = bookService.searchBooks(title, author, category);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    /**
     * Get books by category
     * 
     * @param category Book category
     * @return List of books in the category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Book>>> getBooksByCategory(@PathVariable String category) {
        List<Book> books = bookService.findByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    /**
     * Get books by author
     * 
     * @param author Book author
     * @return List of books by the author
     */
    @GetMapping("/author/{author}")
    public ResponseEntity<ApiResponse<List<Book>>> getBooksByAuthor(@PathVariable String author) {
        List<Book> books = bookService.findByAuthor(author);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    /**
     * Get books by published year
     * 
     * @param year Published year
     * @return List of books published in the year
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<ApiResponse<List<Book>>> getBooksByYear(@PathVariable Integer year) {
        List<Book> books = bookService.findByPublishedYear(year);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    /**
     * Check if book is available
     * 
     * @param id Book ID
     * @param libraryId Library ID
     * @return Boolean indicating if book is available
     */
    @GetMapping("/{id}/available")
    public ResponseEntity<ApiResponse<Boolean>> isBookAvailable(
        @PathVariable Long id,
        @RequestParam Long libraryId
    ) {
        boolean available = bookService.isBookAvailable(id, libraryId);
        return ResponseEntity.ok(ApiResponse.success(available));
    }
    
    /**
     * Get available copy count
     * 
     * @param id Book ID
     * @param libraryId Library ID
     * @return Number of available copies
     */
    @GetMapping("/{id}/available-count")
    public ResponseEntity<ApiResponse<Long>> getAvailableCopyCount(
        @PathVariable Long id,
        @RequestParam Long libraryId
    ) {
        long count = bookService.getAvailableCopyCount(id, libraryId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
