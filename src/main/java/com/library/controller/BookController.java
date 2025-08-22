package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.request.CreateBookRequest;
import com.library.entity.Book;
import com.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Books", description = "Book management endpoints")
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
    @Operation(
        summary = "Get all books",
        description = "Retrieve all books with optional pagination"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Books retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<Book>>> getAllBooks(
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size", example = "10")
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
    @Operation(
        summary = "Get book by ID",
        description = "Retrieve a specific book by its ID"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Book found successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Book not found"
        )
    })
    public ResponseEntity<ApiResponse<Book>> getBook(
        @Parameter(description = "Book ID", required = true, example = "1")
        @PathVariable Long id
    ) {
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
    @Operation(
        summary = "Create new book",
        description = "Create a new book with the provided information"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Book created successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "message": "Book created successfully",
                        "data": {
                            "id": 1,
                            "title": "The Great Gatsby",
                            "author": "F. Scott Fitzgerald",
                            "publishedYear": 1925,
                            "category": "Fiction",
                            "bookType": "圖書"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid input data"
        )
    })
    public ResponseEntity<ApiResponse<Book>> createBook(
        @Parameter(description = "Book creation information", required = true)
        @RequestBody @Valid CreateBookRequest request
    ) {
        Book book = bookService.createBook(
            request.getTitle(),
            request.getAuthor(),
            request.getPublishedYear(),
            request.getCategory(),
            request.getBookType()
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
    @Operation(
        summary = "Search books",
        description = "Search books by title, author, or category"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Search completed successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<Book>>> searchBooks(
        @Parameter(description = "Book title to search for", example = "Gatsby")
        @RequestParam(required = false) String title,
        @Parameter(description = "Book author to search for", example = "Fitzgerald")
        @RequestParam(required = false) String author,
        @Parameter(description = "Book category to search for", example = "Fiction")
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
    @Operation(
        summary = "Get books by category",
        description = "Retrieve all books in a specific category"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Books retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<Book>>> getBooksByCategory(
        @Parameter(description = "Book category", required = true, example = "Fiction")
        @PathVariable String category
    ) {
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
    @Operation(
        summary = "Get books by author",
        description = "Retrieve all books by a specific author"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Books retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<Book>>> getBooksByAuthor(
        @Parameter(description = "Book author", required = true, example = "F. Scott Fitzgerald")
        @PathVariable String author
    ) {
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
    @Operation(
        summary = "Get books by published year",
        description = "Retrieve all books published in a specific year"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Books retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<Book>>> getBooksByYear(
        @Parameter(description = "Published year", required = true, example = "1925")
        @PathVariable Integer year
    ) {
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
    @Operation(
        summary = "Check book availability",
        description = "Check if a book is available for borrowing at a specific library"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Availability checked successfully"
        )
    })
    public ResponseEntity<ApiResponse<Boolean>> isBookAvailable(
        @Parameter(description = "Book ID", required = true, example = "1")
        @PathVariable Long id,
        @Parameter(description = "Library ID", required = true, example = "1")
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
    @Operation(
        summary = "Get available copy count",
        description = "Get the number of available copies of a book at a specific library"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<Long>> getAvailableCopyCount(
        @Parameter(description = "Book ID", required = true, example = "1")
        @PathVariable Long id,
        @Parameter(description = "Library ID", required = true, example = "1")
        @RequestParam Long libraryId
    ) {
        long count = bookService.getAvailableCopyCount(id, libraryId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
