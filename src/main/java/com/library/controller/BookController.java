package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.request.AddBookCopiesRequest;
import com.library.dto.request.CreateBookRequest;
import com.library.dto.request.CreateBookWithCopiesRequest;
import com.library.dto.response.BookResponse;
import com.library.dto.response.SimpleBookResponse;
import com.library.dto.response.BookWithCopiesResponse;
import com.library.dto.response.BookCopyResponse;
import com.library.dto.response.BookWithCopySummaryResponse;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.entity.User;
import com.library.service.BookService;
import com.library.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserService userService;
    
    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }
    
    /**
     * Check if current user is a librarian
     */
    private boolean isLibrarian() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Current user not found"));
        return "LIBRARIAN".equals(currentUser.getRole().getName());
    }
    
    /**
     * Check librarian permissions and throw exception if not authorized
     */
    private void checkLibrarianPermission() {
        if (!isLibrarian()) {
            throw new RuntimeException("Access denied. Only librarians can perform this operation.");
        }
    }
    
    /**
     * Get all books with pagination
     * Access: All authenticated users (MEMBER, LIBRARIAN)
     * 
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @return List of books
     */
    @GetMapping
    @Operation(
        summary = "Get all books",
        description = "Retrieve all books with optional pagination. Accessible by all authenticated users."
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
        List<Book> books = bookService.getAllBooks(page, size);
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
     * Search books with copy summary
     * Access: All authenticated users (MEMBER, LIBRARIAN)
     * 
     * @param title Book title (optional)
     * @param author Book author (optional)
     * @param category Book category (optional)
     * @param libraryId Library ID to filter copies (optional)
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @return List of books with copy summary
     */
    @GetMapping("/search")
    @Operation(
        summary = "Search books with copy summary",
        description = "Search books with copy availability information. Can filter by library. Accessible by all authenticated users."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Books with copy summary retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<BookWithCopySummaryResponse>>> searchBooksWithCopySummary(
        @Parameter(description = "Book title", example = "Java")
        @RequestParam(required = false) String title,
        @Parameter(description = "Book author", example = "John Smith")
        @RequestParam(required = false) String author,
        @Parameter(description = "Book category", example = "Programming")
        @RequestParam(required = false) String category,
        @Parameter(description = "Library ID to filter copies", example = "1")
        @RequestParam(required = false) Long libraryId,
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size", example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        List<BookWithCopySummaryResponse> books = bookService.searchBooksWithCopySummary(
            title, author, category, libraryId, page, size);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    /**
     * Get book by ID
     * Access: All authenticated users (MEMBER, LIBRARIAN)
     * 
     * @param id Book ID
     * @return Book information
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get book by ID",
        description = "Retrieve a specific book by its ID. Accessible by all authenticated users."
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
     * Access: LIBRARIAN only
     * 
     * @param request Book creation request
     * @return Created book information
     */
    @PostMapping
    @Operation(
        summary = "Create new book (basic)",
        description = "Create a new book with the provided information (without copies). LIBRARIAN access only."
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
        // Check librarian permissions
        checkLibrarianPermission();
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
     * Access: LIBRARIAN only
     * 
     * @param request Book creation request with library copies configuration
     * @return Created book information with copies details
     */
    @PostMapping("/with-copies")
    @Operation(
        summary = "Create new book with copies",
        description = "Create a new book with multiple copies distributed across different libraries. LIBRARIAN access only."
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
        // Check librarian permissions
        checkLibrarianPermission();
        BookWithCopiesResponse response = bookService.createBookWithCopies(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Book with copies created successfully"));
    }
    
    /**
     * Add more copies to an existing book across different libraries
     * Access: LIBRARIAN only
     * 
     * @param request Add book copies request
     * @return Updated book information with new copies details
     */
    @PostMapping("/add-copies")
    @Operation(
        summary = "Add copies to existing book",
        description = "Add more copies to an existing book across different libraries. LIBRARIAN access only."
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
        // Check librarian permissions
        checkLibrarianPermission();
        BookWithCopiesResponse response = bookService.addBookCopies(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Book copies added successfully"));
    }
    

    
    /**
     * Get books by category with pagination
     * Access: All authenticated users (MEMBER, LIBRARIAN)
     * 
     * @param category Book category
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @return List of books in the category
     */
    @GetMapping("/category/{category}")
    @Operation(
        summary = "Get books by category",
        description = "Retrieve all books in a specific category with pagination. Accessible by all authenticated users."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Books retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<SimpleBookResponse>>> getBooksByCategory(
        @Parameter(description = "Book category", required = true, example = "Fiction")
        @PathVariable String category,
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size", example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        List<Book> books = bookService.getBooksByCategory(category, page, size);
        List<SimpleBookResponse> bookResponses = books.stream()
            .map(book -> new SimpleBookResponse(
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
     * Get books by author with pagination
     * Access: All authenticated users (MEMBER, LIBRARIAN)
     * 
     * @param author Book author
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @return List of books by the author
     */
    @GetMapping("/author/{author}")
    @Operation(
        summary = "Get books by author",
        description = "Retrieve all books by a specific author with pagination. Accessible by all authenticated users."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Books retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<SimpleBookResponse>>> getBooksByAuthor(
        @Parameter(description = "Book author", required = true, example = "F. Scott Fitzgerald")
        @PathVariable String author,
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size", example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        List<Book> books = bookService.getBooksByAuthor(author, page, size);
        List<SimpleBookResponse> bookResponses = books.stream()
            .map(book -> new SimpleBookResponse(
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
     * Get books by published year with pagination
     * Access: All authenticated users (MEMBER, LIBRARIAN)
     * 
     * @param year Published year
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @return List of books published in the year
     */
    @GetMapping("/year/{year}")
    @Operation(
        summary = "Get books by published year",
        description = "Retrieve all books published in a specific year with pagination. Accessible by all authenticated users."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Books retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<SimpleBookResponse>>> getBooksByYear(
        @Parameter(description = "Published year", required = true, example = "1925")
        @PathVariable Integer year,
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size", example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        List<Book> books = bookService.getBooksByPublishedYear(year, page, size);
        List<SimpleBookResponse> bookResponses = books.stream()
            .map(book -> new SimpleBookResponse(
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
     * Check if book is available
     * Access: All authenticated users (MEMBER, LIBRARIAN)
     * 
     * @param id Book ID
     * @param libraryId Library ID
     * @return Boolean indicating if book is available
     */
    @GetMapping("/{id}/available")
    @Operation(
        summary = "Check book availability",
        description = "Check if a book is available for borrowing at a specific library. Accessible by all authenticated users."
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
     * Access: All authenticated users (MEMBER, LIBRARIAN)
     * 
     * @param id Book ID
     * @param libraryId Library ID
     * @return Number of available copies
     */
    @GetMapping("/{id}/available-count")
    @Operation(
        summary = "Get available copy count",
        description = "Get the number of available copies of a book at a specific library. Accessible by all authenticated users."
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
    
    /**
     * Get book copies for borrowing
     * Access: All authenticated users (MEMBER, LIBRARIAN)
     * 
     * @param id Book ID
     * @return List of book copies with copy IDs for borrowing
     */
    @GetMapping("/{id}/copies")
    @Operation(
        summary = "Get book copies",
        description = "Get all copies of a book across all libraries. Shows copy IDs needed for borrowing. Accessible by all authenticated users."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Book copies retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<BookCopyResponse>>> getBookCopies(
        @Parameter(description = "Book ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        List<BookCopy> bookCopies = bookService.getBookCopies(id);
        List<BookCopyResponse> copyResponses = bookCopies.stream()
            .map(copy -> new BookCopyResponse(
                copy.getId(),
                copy.getBook().getId(),
                copy.getBook().getTitle(),
                copy.getBook().getAuthor(),
                copy.getLibrary().getId(),
                copy.getLibrary().getName(),
                copy.getCopyNumber(),
                copy.getStatus()
            ))
            .toList();
        return ResponseEntity.ok(ApiResponse.success(copyResponses));
    }
    
    /**
     * Get available book copies for borrowing
     * Access: All authenticated users (MEMBER, LIBRARIAN)
     * 
     * @param id Book ID
     * @return List of available book copies with copy IDs for borrowing
     */
    @GetMapping("/{id}/copies/available")
    @Operation(
        summary = "Get available book copies",
        description = "Get only available copies of a book across all libraries. Shows copy IDs needed for borrowing. Accessible by all authenticated users."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Available book copies retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<BookCopyResponse>>> getAvailableBookCopies(
        @Parameter(description = "Book ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        List<BookCopy> bookCopies = bookService.getAvailableBookCopies(id);
        List<BookCopyResponse> copyResponses = bookCopies.stream()
            .map(copy -> new BookCopyResponse(
                copy.getId(),
                copy.getBook().getId(),
                copy.getBook().getTitle(),
                copy.getBook().getAuthor(),
                copy.getLibrary().getId(),
                copy.getLibrary().getName(),
                copy.getCopyNumber(),
                copy.getStatus()
            ))
            .toList();
        return ResponseEntity.ok(ApiResponse.success(copyResponses));
    }
}
