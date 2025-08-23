package com.library.service;

import com.library.dto.request.AddBookCopiesRequest;
import com.library.dto.request.CreateBookWithCopiesRequest;
import com.library.dto.response.BookWithCopiesResponse;
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
