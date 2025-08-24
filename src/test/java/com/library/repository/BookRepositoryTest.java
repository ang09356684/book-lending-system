package com.library.repository;

import com.library.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BookRepository Integration Test
 * Uses H2 in-memory database for real database operation testing
 * 
 * @author Library System
 * @version 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
public class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    private Book testBook1;
    private Book testBook2;
    private Book testBook3;

    @BeforeEach
    void setUp() {
        // Clean up database
        entityManager.clear();

        // Create test books
        testBook1 = new Book();
        testBook1.setTitle("Java Programming");
        testBook1.setAuthor("John Smith");
        testBook1.setPublishedYear(2023);
        testBook1.setCategory("Programming");
        testBook1.setBookType("圖書");
        testBook1 = entityManager.persistAndFlush(testBook1);

        testBook2 = new Book();
        testBook2.setTitle("Python for Beginners");
        testBook2.setAuthor("Jane Doe");
        testBook2.setPublishedYear(2022);
        testBook2.setCategory("Programming");
        testBook2.setBookType("書籍");
        testBook2 = entityManager.persistAndFlush(testBook2);

        testBook3 = new Book();
        testBook3.setTitle("The Great Gatsby");
        testBook3.setAuthor("F. Scott Fitzgerald");
        testBook3.setPublishedYear(1925);
        testBook3.setCategory("Fiction");
        testBook3.setBookType("圖書");
        testBook3 = entityManager.persistAndFlush(testBook3);

        entityManager.flush();
    }

    @Test
    void testFindById_Success() {
        // Act
        Book result = bookRepository.findById(testBook1.getId()).orElse(null);

        // Assert
        assertNotNull(result);
        assertEquals("Java Programming", result.getTitle());
        assertEquals("John Smith", result.getAuthor());
        assertEquals(2023, result.getPublishedYear());
        assertEquals("Programming", result.getCategory());
        assertEquals("圖書", result.getBookType());
    }

    @Test
    void testFindById_NotFound() {
        // Act
        Book result = bookRepository.findById(999L).orElse(null);

        // Assert
        assertNull(result);
    }

    @Test
    void testSearchBooks_ByTitle() {
        // Act
        List<Book> results = bookRepository.searchBooks("Java", null, null);

        // Assert
        assertEquals(1, results.size());
        assertEquals("Java Programming", results.get(0).getTitle());
    }

    @Test
    void testSearchBooks_ByAuthor() {
        // Act
        List<Book> results = bookRepository.searchBooks(null, "Smith", null);

        // Assert
        assertEquals(1, results.size());
        assertEquals("John Smith", results.get(0).getAuthor());
    }

    @Test
    void testSearchBooks_ByPublishedYear() {
        // Act
        List<Book> results = bookRepository.searchBooks(null, null, 2023);

        // Assert
        assertEquals(1, results.size());
        assertEquals(2023, results.get(0).getPublishedYear());
    }

    @Test
    void testSearchBooks_ByTitleAndAuthor() {
        // Act
        List<Book> results = bookRepository.searchBooks("Python", "Doe", null);

        // Assert
        assertEquals(1, results.size());
        assertEquals("Python for Beginners", results.get(0).getTitle());
        assertEquals("Jane Doe", results.get(0).getAuthor());
    }

    @Test
    void testSearchBooks_NoResults() {
        // Act
        List<Book> results = bookRepository.searchBooks("Nonexistent", null, null);

        // Assert
        assertEquals(0, results.size());
    }

    @Test
    void testSearchBooks_AllNull() {
        // Act
        List<Book> results = bookRepository.searchBooks(null, null, null);

        // Assert
        assertEquals(3, results.size());
    }

    @Test
    void testFindByCategory_Success() {
        // Act
        List<Book> programmingBooks = bookRepository.findByCategory("Programming");
        List<Book> fictionBooks = bookRepository.findByCategory("Fiction");

        // Assert
        assertEquals(2, programmingBooks.size());
        assertEquals(1, fictionBooks.size());
        
        // Verify Programming category books
        assertTrue(programmingBooks.stream().anyMatch(book -> "Java Programming".equals(book.getTitle())));
        assertTrue(programmingBooks.stream().anyMatch(book -> "Python for Beginners".equals(book.getTitle())));
        
        // Verify Fiction category books
        assertEquals("The Great Gatsby", fictionBooks.get(0).getTitle());
    }

    @Test
    void testFindByAuthor_Success() {
        // Act
        List<Book> smithBooks = bookRepository.findByAuthor("John Smith");
        List<Book> doeBooks = bookRepository.findByAuthor("Jane Doe");

        // Assert
        assertEquals(1, smithBooks.size());
        assertEquals("Java Programming", smithBooks.get(0).getTitle());
        
        assertEquals(1, doeBooks.size());
        assertEquals("Python for Beginners", doeBooks.get(0).getTitle());
    }

    @Test
    void testSave_NewBook() {
        // Arrange
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setAuthor("New Author");
        newBook.setPublishedYear(2024);
        newBook.setCategory("Science");
        newBook.setBookType("書籍");

        // Act
        Book savedBook = bookRepository.save(newBook);

        // Assert
        assertNotNull(savedBook.getId());
        assertEquals("New Book", savedBook.getTitle());
        assertEquals("New Author", savedBook.getAuthor());
        assertEquals(2024, savedBook.getPublishedYear());
        assertEquals("Science", savedBook.getCategory());
        assertEquals("書籍", savedBook.getBookType());
    }

    @Test
    void testSave_UpdateBook() {
        // Arrange
        testBook1.setTitle("Updated Java Programming");
        testBook1.setPublishedYear(2024);

        // Act
        Book updatedBook = bookRepository.save(testBook1);

        // Assert
        assertEquals("Updated Java Programming", updatedBook.getTitle());
        assertEquals(2024, updatedBook.getPublishedYear());
        
        // Verify that data in database is actually updated
        Book foundBook = bookRepository.findById(testBook1.getId()).orElse(null);
        assertNotNull(foundBook);
        assertEquals("Updated Java Programming", foundBook.getTitle());
        assertEquals(2024, foundBook.getPublishedYear());
    }

    @Test
    void testDeleteBook() {
        // Arrange
        Long bookId = testBook1.getId();

        // Act
        bookRepository.deleteById(bookId);

        // Assert
        Book deletedBook = bookRepository.findById(bookId).orElse(null);
        assertNull(deletedBook);
    }

    @Test
    void testFindAll() {
        // Act
        List<Book> allBooks = bookRepository.findAll();

        // Assert
        assertEquals(3, allBooks.size());
        assertTrue(allBooks.stream().anyMatch(book -> "Java Programming".equals(book.getTitle())));
        assertTrue(allBooks.stream().anyMatch(book -> "Python for Beginners".equals(book.getTitle())));
        assertTrue(allBooks.stream().anyMatch(book -> "The Great Gatsby".equals(book.getTitle())));
    }
}
