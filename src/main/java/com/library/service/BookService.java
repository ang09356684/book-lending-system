package com.library.service;

import com.library.dto.request.AddBookCopiesRequest;
import com.library.dto.request.CreateBookWithCopiesRequest;
import com.library.dto.response.BookWithCopiesResponse;
import com.library.dto.response.BookWithCopySummaryResponse;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.entity.Library;
import com.library.repository.BookCopyRepository;
import com.library.repository.BookRepository;
import com.library.repository.LibraryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        
        // Create book with default type "圖書"
        Book book = new Book(title, author, publishedYear, category);
        return bookRepository.save(book);
    }
    
    /**
     * Create a new book with specific type
     */
    public Book createBook(String title, String author, Integer publishedYear, String category, String bookType) {
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
        
        if (bookType == null || bookType.trim().isEmpty()) {
            throw new RuntimeException("Book type is required");
        }
        
        // Validate book type
        if (!"圖書".equals(bookType) && !"書籍".equals(bookType)) {
            throw new RuntimeException("Book type must be either '圖書' or '書籍'");
        }
        
        // Create book with specified type
        Book book = new Book(title, author, publishedYear, category, bookType);
        return bookRepository.save(book);
    }
    
    /**
     * Search books
     */
    public List<Book> searchBooks(String title, String author, Integer publishedYear) {
        return bookRepository.searchBooks(title, author, publishedYear);
    }
    
    /**
     * Get all books with pagination
     */
    public List<Book> getAllBooks(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
        
        // Calculate offset
        int offset = page * size;
        
        // Get all books first (since we don't have a paginated search method)
        List<Book> allBooks = bookRepository.searchBooks(null, null, null);
        
        // Apply pagination manually
        int startIndex = offset;
        int endIndex = Math.min(startIndex + size, allBooks.size());
        
        if (startIndex >= allBooks.size()) {
            return new ArrayList<>();
        }
        
        return allBooks.subList(startIndex, endIndex);
    }
    
    /**
     * Find books by category
     */
    public List<Book> findByCategory(String category) {
        return bookRepository.findByCategory(category);
    }
    
    /**
     * Get books by category with pagination
     */
    public List<Book> getBooksByCategory(String category, int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
        
        // Calculate offset
        int offset = page * size;
        
        // Get all books in category first
        List<Book> allBooks = bookRepository.findByCategory(category);
        
        // Apply pagination manually
        int startIndex = offset;
        int endIndex = Math.min(startIndex + size, allBooks.size());
        
        if (startIndex >= allBooks.size()) {
            return new ArrayList<>();
        }
        
        return allBooks.subList(startIndex, endIndex);
    }
    
    /**
     * Find books by author
     */
    public List<Book> findByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }
    
    /**
     * Get books by author with pagination
     */
    public List<Book> getBooksByAuthor(String author, int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
        
        // Calculate offset
        int offset = page * size;
        
        // Get all books by author first
        List<Book> allBooks = bookRepository.findByAuthor(author);
        
        // Apply pagination manually
        int startIndex = offset;
        int endIndex = Math.min(startIndex + size, allBooks.size());
        
        if (startIndex >= allBooks.size()) {
            return new ArrayList<>();
        }
        
        return allBooks.subList(startIndex, endIndex);
    }
    
    /**
     * Find books by published year
     */
    public List<Book> findByPublishedYear(Integer publishedYear) {
        return bookRepository.findByPublishedYear(publishedYear);
    }
    
    /**
     * Get books by published year with pagination
     */
    public List<Book> getBooksByPublishedYear(Integer publishedYear, int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
        
        // Calculate offset
        int offset = page * size;
        
        // Get all books by published year first
        List<Book> allBooks = bookRepository.findByPublishedYear(publishedYear);
        
        // Apply pagination manually
        int startIndex = offset;
        int endIndex = Math.min(startIndex + size, allBooks.size());
        
        if (startIndex >= allBooks.size()) {
            return new ArrayList<>();
        }
        
        return allBooks.subList(startIndex, endIndex);
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
    
    /**
     * Get book copies for a specific book
     */
    public List<BookCopy> getBookCopies(Long bookId) {
        Book book = findById(bookId);
        return bookCopyRepository.findByBook(book);
    }
    
    /**
     * Get available book copies across all libraries
     */
    public List<BookCopy> getAvailableBookCopies(Long bookId) {
        Book book = findById(bookId);
        return bookCopyRepository.findByBookAndStatus(book, "AVAILABLE");
    }
    
    /**
     * Search books with copy summary
     */
    public List<BookWithCopySummaryResponse> searchBooksWithCopySummary(
            String title, String author, Integer publishedYear, Long libraryId, int page, int size) {
        
        // Get books based on search criteria
        List<Book> books = bookRepository.searchBooks(title, author, publishedYear);
        
        // Apply pagination
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        int offset = page * size;
        int startIndex = offset;
        int endIndex = Math.min(startIndex + size, books.size());
        
        if (startIndex >= books.size()) {
            return new ArrayList<>();
        }
        
        List<Book> paginatedBooks = books.subList(startIndex, endIndex);
        
        // Convert to response with copy summary
        return paginatedBooks.stream()
            .map(book -> createBookWithCopySummary(book, libraryId))
            .toList();
    }
    
    /**
     * Create book with copy summary response
     */
    private BookWithCopySummaryResponse createBookWithCopySummary(Book book, Long libraryId) {
        List<BookCopy> allCopies = bookCopyRepository.findByBook(book);
        
        // Filter by library if specified
        List<BookCopy> relevantCopies = libraryId != null 
            ? allCopies.stream()
                .filter(copy -> copy.getLibrary().getId().equals(libraryId))
                .toList()
            : allCopies;
        
        // Calculate statistics
        int totalCopies = relevantCopies.size();
        int availableCopies = (int) relevantCopies.stream()
            .filter(copy -> "AVAILABLE".equals(copy.getStatus()))
            .count();
        
        // Group by library
        Map<Long, List<BookCopy>> copiesByLibrary = relevantCopies.stream()
            .collect(Collectors.groupingBy(copy -> copy.getLibrary().getId()));
        
        List<BookWithCopySummaryResponse.LibraryCopySummary> librarySummaries = copiesByLibrary.entrySet().stream()
            .map(entry -> {
                Long libId = entry.getKey();
                List<BookCopy> libCopies = entry.getValue();
                String libName = libCopies.get(0).getLibrary().getName();
                int libTotal = libCopies.size();
                int libAvailable = (int) libCopies.stream()
                    .filter(copy -> "AVAILABLE".equals(copy.getStatus()))
                    .count();
                
                return new BookWithCopySummaryResponse.LibraryCopySummary(libId, libName, libTotal, libAvailable);
            })
            .toList();
        
        BookWithCopySummaryResponse.CopySummary copySummary = new BookWithCopySummaryResponse.CopySummary(
            totalCopies, availableCopies, librarySummaries);
        
        return new BookWithCopySummaryResponse(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getPublishedYear(),
            book.getCategory(),
            book.getBookType(),
            copySummary
        );
    }
    
    /**
     * Create a new book with multiple copies across different libraries
     * Supports multi-library collections and multiple copies per library
     */
    public BookWithCopiesResponse createBookWithCopies(CreateBookWithCopiesRequest request) {
        // Validate input
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Book title is required");
        }
        
        if (request.getAuthor() == null || request.getAuthor().trim().isEmpty()) {
            throw new RuntimeException("Book author is required");
        }
        
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new RuntimeException("Book category is required");
        }
        
        if (request.getBookType() == null || request.getBookType().trim().isEmpty()) {
            throw new RuntimeException("Book type is required");
        }
        
        // Validate book type
        if (!"圖書".equals(request.getBookType()) && !"書籍".equals(request.getBookType())) {
            throw new RuntimeException("Book type must be either '圖書' or '書籍'");
        }
        
        if (request.getLibraryCopies() == null || request.getLibraryCopies().isEmpty()) {
            throw new RuntimeException("At least one library copy configuration is required");
        }
        
        // Create the book
        Book book = new Book(
            request.getTitle(),
            request.getAuthor(),
            request.getPublishedYear(),
            request.getCategory(),
            request.getBookType()
        );
        book = bookRepository.save(book);
        
        // Create copies for each library
        List<BookWithCopiesResponse.LibraryCopyInfo> libraryCopyInfos = new ArrayList<>();
        
        for (CreateBookWithCopiesRequest.LibraryCopyConfig config : request.getLibraryCopies()) {
            // Validate library exists
            Library library = libraryRepository.findById(config.getLibraryId())
                .orElseThrow(() -> new RuntimeException("Library not found with ID: " + config.getLibraryId()));
            
            if (config.getNumberOfCopies() == null || config.getNumberOfCopies() <= 0) {
                throw new RuntimeException("Number of copies must be greater than 0 for library: " + library.getName());
            }
            
            // Create copies for this library
            List<BookWithCopiesResponse.CopyInfo> copyInfos = new ArrayList<>();
            
            for (int i = 1; i <= config.getNumberOfCopies(); i++) {
                BookCopy copy = new BookCopy();
                copy.setBook(book);
                copy.setLibrary(library);
                copy.setCopyNumber(i);
                copy.setStatus("AVAILABLE");
                
                copy = bookCopyRepository.save(copy);
                
                copyInfos.add(new BookWithCopiesResponse.CopyInfo(
                    copy.getId(),
                    copy.getCopyNumber(),
                    copy.getStatus()
                ));
            }
            
            libraryCopyInfos.add(new BookWithCopiesResponse.LibraryCopyInfo(
                library.getId(),
                library.getName(),
                config.getNumberOfCopies(),
                copyInfos
            ));
        }
        
        return new BookWithCopiesResponse(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getPublishedYear(),
            book.getCategory(),
            book.getBookType(),
            libraryCopyInfos
        );
    }
    
    /**
     * Add more copies to an existing book across different libraries
     * Supports adding copies to existing libraries or new libraries
     */
    public BookWithCopiesResponse addBookCopies(AddBookCopiesRequest request) {
        // Validate book exists
        Book book = findById(request.getBookId());
        
        if (request.getLibraryCopies() == null || request.getLibraryCopies().isEmpty()) {
            throw new RuntimeException("At least one library copy configuration is required");
        }
        
        // Create copies for each library
        List<BookWithCopiesResponse.LibraryCopyInfo> libraryCopyInfos = new ArrayList<>();
        
        for (AddBookCopiesRequest.LibraryCopyConfig config : request.getLibraryCopies()) {
            // Validate library exists
            Library library = libraryRepository.findById(config.getLibraryId())
                .orElseThrow(() -> new RuntimeException("Library not found with ID: " + config.getLibraryId()));
            
            if (config.getNumberOfCopies() == null || config.getNumberOfCopies() <= 0) {
                throw new RuntimeException("Number of copies must be greater than 0 for library: " + library.getName());
            }
            
            // Get the next copy number for this book in this library
            int nextCopyNumber = getNextCopyNumber(book.getId(), library.getId());
            
            // Create copies for this library
            List<BookWithCopiesResponse.CopyInfo> copyInfos = new ArrayList<>();
            
            for (int i = 0; i < config.getNumberOfCopies(); i++) {
                BookCopy copy = new BookCopy();
                copy.setBook(book);
                copy.setLibrary(library);
                copy.setCopyNumber(nextCopyNumber + i);
                copy.setStatus("AVAILABLE");
                
                copy = bookCopyRepository.save(copy);
                
                copyInfos.add(new BookWithCopiesResponse.CopyInfo(
                    copy.getId(),
                    copy.getCopyNumber(),
                    copy.getStatus()
                ));
            }
            
            libraryCopyInfos.add(new BookWithCopiesResponse.LibraryCopyInfo(
                library.getId(),
                library.getName(),
                config.getNumberOfCopies(),
                copyInfos
            ));
        }
        
        return new BookWithCopiesResponse(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getPublishedYear(),
            book.getCategory(),
            book.getBookType(),
            libraryCopyInfos
        );
    }
    
    /**
     * Update book information
     */
    public Book updateBook(Long bookId, String title, String author, Integer publishedYear, String category, String bookType) {
        Book book = findById(bookId);
        
        // Validate book type
        if (bookType != null && !"圖書".equals(bookType) && !"書籍".equals(bookType)) {
            throw new RuntimeException("Book type must be either '圖書' or '書籍'");
        }
        
        // Update fields if provided
        if (title != null && !title.trim().isEmpty()) {
            book.setTitle(title);
        }
        
        if (author != null && !author.trim().isEmpty()) {
            book.setAuthor(author);
        }
        
        if (publishedYear != null && publishedYear > 0) {
            book.setPublishedYear(publishedYear);
        }
        
        if (category != null && !category.trim().isEmpty()) {
            book.setCategory(category);
        }
        
        if (bookType != null && !bookType.trim().isEmpty()) {
            book.setBookType(bookType);
        }
        
        return bookRepository.save(book);
    }
    
    /**
     * Update book copy information
     */
    public BookCopy updateBookCopy(Long copyId, Integer copyNumber, String status) {
        BookCopy bookCopy = bookCopyRepository.findById(copyId)
            .orElseThrow(() -> new RuntimeException("Book copy not found"));
        
        // Validate status
        if (status != null && !status.matches("AVAILABLE|BORROWED|LOST|DAMAGED")) {
            throw new RuntimeException("Invalid status. Must be AVAILABLE, BORROWED, LOST, or DAMAGED");
        }
        
        // Update copy number if provided
        if (copyNumber != null && copyNumber > 0) {
            // Check if copy number already exists for this book in this library
            List<BookCopy> existingCopies = bookCopyRepository.findByBookIdAndLibraryId(
                bookCopy.getBook().getId(), bookCopy.getLibrary().getId());
            
            boolean copyNumberExists = existingCopies.stream()
                .anyMatch(copy -> !copy.getId().equals(copyId) && copy.getCopyNumber().equals(copyNumber));
            
            if (copyNumberExists) {
                throw new RuntimeException("Copy number already exists for this book in this library");
            }
            
            bookCopy.setCopyNumber(copyNumber);
        }
        
        // Update status if provided
        if (status != null && !status.trim().isEmpty()) {
            bookCopy.setStatus(status);
        }
        
        return bookCopyRepository.save(bookCopy);
    }
    
    /**
     * Get the next copy number for a book in a specific library
     */
    private int getNextCopyNumber(Long bookId, Long libraryId) {
        // Find the highest copy number for this book in this library
        List<BookCopy> existingCopies = bookCopyRepository.findByBookIdAndLibraryId(bookId, libraryId);
        
        if (existingCopies.isEmpty()) {
            return 1; // First copy in this library
        }
        
        // Find the highest copy number and add 1
        int maxCopyNumber = existingCopies.stream()
            .mapToInt(BookCopy::getCopyNumber)
            .max()
            .orElse(0);
        
        return maxCopyNumber + 1;
    }
}
