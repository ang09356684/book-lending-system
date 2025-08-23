package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.request.AddBookCopiesRequest;
import com.library.dto.request.CreateBookRequest;
import com.library.dto.request.CreateBookWithCopiesRequest;
import com.library.dto.response.BookResponse;
import com.library.dto.response.BookWithCopiesResponse;
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
@RequestMapping("/books")
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
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks(
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size", example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        List<Book> books = bookService.searchBooks(null, null, null);
        List<BookResponse> bookResponses = books.stream()
            .map(book -> new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublishedYear(),
                book.getCategory(),
                book.getBookType()
            ))
            .toList();
        return ResponseEntity.ok(ApiResponse.success(bookResponses));
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
    public ResponseEntity<ApiResponse<BookResponse>> getBook(
        @Parameter(description = "Book ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        Book book = bookService.findById(id);
        BookResponse bookResponse = new BookResponse(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getPublishedYear(),
            book.getCategory(),
            book.getBookType()
        );
        return ResponseEntity.ok(ApiResponse.success(bookResponse));
    }
    
    /**
     * Create a new book (basic version - only creates book without copies)
     * 
     * @param request Book creation request
     * @return Created book information
     */
    @PostMapping
    @Operation(
        summary = "Create new book (basic)",
        description = "Create a new book with the provided information (without copies)"
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
    public ResponseEntity<ApiResponse<BookResponse>> createBook(
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
        
        BookResponse bookResponse = new BookResponse(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getPublishedYear(),
            book.getCategory(),
            book.getBookType()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(bookResponse, "Book created successfully"));
    }
    
    /**
     * Create a new book with multiple copies across different libraries
     * Supports multi-library collections and multiple copies per library
     * 
     * @param request Book creation request with library copies configuration
     * @return Created book information with copies details
     */
    @PostMapping("/with-copies")
    @Operation(
        summary = "Create new book with copies",
        description = "Create a new book with multiple copies distributed across different libraries"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Book with copies created successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "message": "Book with copies created successfully",
                        "data": {
                            "id": 1,
                            "title": "Java Programming Guide",
                            "author": "John Smith",
                            "publishedYear": 2023,
                            "category": "Programming",
                            "bookType": "圖書",
                            "libraryCopies": [
                                {
                                    "libraryId": 1,
                                    "libraryName": "Central Library",
                                    "numberOfCopies": 2,
                                    "copies": [
                                        {"copyId": 1, "copyNumber": 1, "status": "AVAILABLE"},
                                        {"copyId": 2, "copyNumber": 2, "status": "AVAILABLE"}
                                    ]
                                }
                            ]
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
    public ResponseEntity<ApiResponse<BookWithCopiesResponse>> createBookWithCopies(
        @Parameter(description = "Book creation information with library copies", required = true)
        @RequestBody @Valid CreateBookWithCopiesRequest request
    ) {
        BookWithCopiesResponse response = bookService.createBookWithCopies(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Book with copies created successfully"));
    }
    
    /**
     * Add more copies to an existing book across different libraries
     * 
     * @param request Add book copies request
     * @return Updated book information with new copies details
     */
    @PostMapping("/add-copies")
    @Operation(
        summary = "Add copies to existing book",
        description = "Add more copies to an existing book across different libraries"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Book copies added successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "message": "Book copies added successfully",
                        "data": {
                            "id": 1,
                            "title": "Java Programming Guide",
                            "author": "John Smith",
                            "publishedYear": 2023,
                            "category": "Programming",
                            "bookType": "圖書",
                            "libraryCopies": [
                                {
                                    "libraryId": 1,
                                    "libraryName": "Central Library",
                                    "numberOfCopies": 2,
                                    "copies": [
                                        {"copyId": 18, "copyNumber": 3, "status": "AVAILABLE"},
                                        {"copyId": 19, "copyNumber": 4, "status": "AVAILABLE"}
                                    ]
                                }
                            ]
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid input data"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Book not found"
        )
    })
    public ResponseEntity<ApiResponse<BookWithCopiesResponse>> addBookCopies(
        @Parameter(description = "Add book copies information", required = true)
        @RequestBody @Valid AddBookCopiesRequest request
    ) {
        BookWithCopiesResponse response = bookService.addBookCopies(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Book copies added successfully"));
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
    public ResponseEntity<ApiResponse<List<BookResponse>>> searchBooks(
        @Parameter(description = "Book title to search for", example = "Gatsby")
        @RequestParam(required = false) String title,
        @Parameter(description = "Book author to search for", example = "Fitzgerald")
        @RequestParam(required = false) String author,
        @Parameter(description = "Book category to search for", example = "Fiction")
        @RequestParam(required = false) String category
    ) {
        List<Book> books = bookService.searchBooks(title, author, category);
        List<BookResponse> bookResponses = books.stream()
            .map(book -> new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublishedYear(),
                book.getCategory(),
                book.getBookType()
            ))
            .toList();
        return ResponseEntity.ok(ApiResponse.success(bookResponses));
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
