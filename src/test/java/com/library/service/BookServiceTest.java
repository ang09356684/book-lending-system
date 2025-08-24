package com.library.service;

import com.library.constant.BookType;
import com.library.entity.Book;
import com.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * BookService Unit Test
 * 
 * @author Library System
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        // Create test data
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setPublishedYear(2023);
        testBook.setCategory("FICTION");
        testBook.setBookType(BookType.TRADITIONAL);
        
        // Reset mocks
        reset(bookRepository);
    }

    @Test
    @DisplayName("Test find by ID - Success")
    void testFindById_Success() {
        // Arrange
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        // Act
        Book result = bookService.findById(bookId);

        // Assert
        assertNotNull(result);
        assertEquals(testBook.getId(), result.getId());
        assertEquals(testBook.getTitle(), result.getTitle());
        verify(bookRepository).findById(bookId);
    }

    @Test
    @DisplayName("Test find by ID - Not found")
    void testFindById_NotFound() {
        // Arrange
        Long bookId = 999L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.findById(bookId);
        });

        assertEquals("Book not found", exception.getMessage());
        verify(bookRepository).findById(bookId);
    }
    
    @Test
    @DisplayName("Test create book - Success")
    void testCreateBook_Success() {
        // Arrange
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        
        // Act
        Book result = bookService.createBook("Test Book", "Test Author", 2023, "FICTION");
        
        // Assert
        assertNotNull(result);
        assertEquals(testBook.getId(), result.getId());
        assertEquals(testBook.getTitle(), result.getTitle());
        verify(bookRepository).save(any(Book.class));
    }
    
    @Test
    @DisplayName("Test search books - Success")
    void testSearchBooks_Success() {
        // Arrange
        String title = "Test";
        String author = "Test Author";
        Integer publishedYear = 2023;
        List<Book> expectedBooks = Arrays.asList(testBook);
        when(bookRepository.searchBooks(title, author, publishedYear)).thenReturn(expectedBooks);
        
        // Act
        List<Book> result = bookService.searchBooks(title, author, publishedYear);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBook, result.get(0));
        verify(bookRepository).searchBooks(title, author, publishedYear);
    }
    
    @Test
    @DisplayName("Test find by category")
    void testFindByCategory_Success() {
        // Arrange
        String category = "FICTION";
        List<Book> expectedBooks = Arrays.asList(testBook);
        when(bookRepository.findByCategory(category)).thenReturn(expectedBooks);
        
        // Act
        List<Book> result = bookService.findByCategory(category);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBook, result.get(0));
        verify(bookRepository).findByCategory(category);
    }
    
    @Test
    @DisplayName("Test find by author")
    void testFindByAuthor_Success() {
        // Arrange
        String author = "Test Author";
        List<Book> expectedBooks = Arrays.asList(testBook);
        when(bookRepository.findByAuthor(author)).thenReturn(expectedBooks);
        
        // Act
        List<Book> result = bookService.findByAuthor(author);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBook, result.get(0));
        verify(bookRepository).findByAuthor(author);
    }
    
    @Test
    @DisplayName("Test count by category")
    void testCountByCategory_Success() {
        // Arrange
        String category = "FICTION";
        when(bookRepository.countByCategory(category)).thenReturn(5L);
        
        // Act
        long result = bookService.countByCategory(category);
        
        // Assert
        assertEquals(5L, result);
        verify(bookRepository).countByCategory(category);
    }
}
