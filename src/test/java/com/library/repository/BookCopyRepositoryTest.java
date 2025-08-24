package com.library.repository;

import com.library.constant.BookType;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.entity.Library;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BookCopyRepository Test
 * Tests database operations for BookCopy entity
 * 
 * @author Library System
 * @version 1.0.0
 */
@DataJpaTest
public class BookCopyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    private Book testBook;
    private Library testLibrary;
    private BookCopy testBookCopy;

    @BeforeEach
    void setUp() {
        // Setup test data
        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setPublishedYear(2023);
        testBook.setCategory("Fiction");
        testBook.setBookType(BookType.TRADITIONAL);
        testBook = entityManager.persistAndFlush(testBook);

        testLibrary = new Library();
        testLibrary.setName("Test Library");
        testLibrary.setAddress("Test Address");
        testLibrary.setPhone("123-456-7890");
        testLibrary = entityManager.persistAndFlush(testLibrary);

        testBookCopy = new BookCopy();
        testBookCopy.setBook(testBook);
        testBookCopy.setLibrary(testLibrary);
        testBookCopy.setCopyNumber(1);
        testBookCopy.setStatus("AVAILABLE");
        testBookCopy = entityManager.persistAndFlush(testBookCopy);
    }

    @Test
    void testFindByBook_Success() {
        // Act
        List<BookCopy> bookCopies = bookCopyRepository.findByBook(testBook);

        // Assert
        assertNotNull(bookCopies);
        assertEquals(1, bookCopies.size());
        assertEquals(testBook.getId(), bookCopies.get(0).getBook().getId());
    }

    @Test
    void testFindByBook_NoCopies() {
        // Arrange - Create a new book with no copies
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setAuthor("New Author");
        newBook.setPublishedYear(2024);
        newBook.setCategory("Science Fiction");
        newBook.setBookType(BookType.MODERN);
        newBook = entityManager.persistAndFlush(newBook);

        // Act
        List<BookCopy> bookCopies = bookCopyRepository.findByBook(newBook);

        // Assert
        assertNotNull(bookCopies);
        assertTrue(bookCopies.isEmpty());
    }

    @Test
    void testFindByBookAndStatus_Success() {
        // Act
        List<BookCopy> bookCopies = bookCopyRepository.findByBookAndStatus(testBook, "AVAILABLE");

        // Assert
        assertNotNull(bookCopies);
        assertEquals(1, bookCopies.size());
        assertEquals("AVAILABLE", bookCopies.get(0).getStatus());
    }

    @Test
    void testFindByBookAndStatus_NoCopies() {
        // Act
        List<BookCopy> bookCopies = bookCopyRepository.findByBookAndStatus(testBook, "BORROWED");

        // Assert
        assertNotNull(bookCopies);
        assertTrue(bookCopies.isEmpty());
    }

    @Test
    void testFindByLibrary_Success() {
        // Act
        List<BookCopy> bookCopies = bookCopyRepository.findByLibrary(testLibrary);

        // Assert
        assertNotNull(bookCopies);
        assertEquals(1, bookCopies.size());
        assertEquals(testLibrary.getId(), bookCopies.get(0).getLibrary().getId());
    }

    @Test
    void testFindByLibrary_NoCopies() {
        // Arrange - Create a new library with no copies
        Library newLibrary = new Library();
        newLibrary.setName("New Library");
        newLibrary.setAddress("New Address");
        newLibrary.setPhone("987-654-3210");
        newLibrary = entityManager.persistAndFlush(newLibrary);

        // Act
        List<BookCopy> bookCopies = bookCopyRepository.findByLibrary(newLibrary);

        // Assert
        assertNotNull(bookCopies);
        assertTrue(bookCopies.isEmpty());
    }

    @Test
    void testFindByStatus_Success() {
        // Act
        List<BookCopy> bookCopies = bookCopyRepository.findByStatus("AVAILABLE");

        // Assert
        assertNotNull(bookCopies);
        assertEquals(1, bookCopies.size());
        assertEquals("AVAILABLE", bookCopies.get(0).getStatus());
    }

    @Test
    void testFindByStatus_NoCopies() {
        // Act
        List<BookCopy> bookCopies = bookCopyRepository.findByStatus("BORROWED");

        // Assert
        assertNotNull(bookCopies);
        assertTrue(bookCopies.isEmpty());
    }

    @Test
    void testFindByBookIdAndLibraryId_Success() {
        // Act
        List<BookCopy> bookCopies = bookCopyRepository.findByBookIdAndLibraryId(testBook.getId(), testLibrary.getId());

        // Assert
        assertNotNull(bookCopies);
        assertEquals(1, bookCopies.size());
        assertEquals(testBook.getId(), bookCopies.get(0).getBook().getId());
        assertEquals(testLibrary.getId(), bookCopies.get(0).getLibrary().getId());
    }

    @Test
    void testFindByBookIdAndLibraryId_NoCopies() {
        // Arrange - Create a new library
        Library newLibrary = new Library();
        newLibrary.setName("New Library");
        newLibrary.setAddress("New Address");
        newLibrary.setPhone("987-654-3210");
        newLibrary = entityManager.persistAndFlush(newLibrary);

        // Act
        List<BookCopy> bookCopies = bookCopyRepository.findByBookIdAndLibraryId(testBook.getId(), newLibrary.getId());

        // Assert
        assertNotNull(bookCopies);
        assertTrue(bookCopies.isEmpty());
    }

    @Test
    void testSave_NewBookCopy() {
        // Arrange
        BookCopy newBookCopy = new BookCopy();
        newBookCopy.setBook(testBook);
        newBookCopy.setLibrary(testLibrary);
        newBookCopy.setCopyNumber(2);
        newBookCopy.setStatus("AVAILABLE");

        // Act
        BookCopy savedCopy = bookCopyRepository.save(newBookCopy);

        // Assert
        assertNotNull(savedCopy);
        assertNotNull(savedCopy.getId());
        assertEquals(testBook.getId(), savedCopy.getBook().getId());
        assertEquals(testLibrary.getId(), savedCopy.getLibrary().getId());
        assertEquals(2, savedCopy.getCopyNumber());
        assertEquals("AVAILABLE", savedCopy.getStatus());
    }

    @Test
    void testSave_UpdateBookCopy() {
        // Arrange
        testBookCopy.setStatus("BORROWED");

        // Act
        BookCopy updatedCopy = bookCopyRepository.save(testBookCopy);

        // Assert
        assertNotNull(updatedCopy);
        assertEquals("BORROWED", updatedCopy.getStatus());
    }

    @Test
    void testFindById_Success() {
        // Act
        Optional<BookCopy> foundCopy = bookCopyRepository.findById(testBookCopy.getId());

        // Assert
        assertTrue(foundCopy.isPresent());
        assertEquals(testBookCopy.getId(), foundCopy.get().getId());
        assertEquals(testBook.getId(), foundCopy.get().getBook().getId());
    }

    @Test
    void testFindById_NotFound() {
        // Act
        Optional<BookCopy> foundCopy = bookCopyRepository.findById(999L);

        // Assert
        assertFalse(foundCopy.isPresent());
    }

    @Test
    void testCountByBook() {
        // Act
        long count = bookCopyRepository.countByBook(testBook);

        // Assert
        assertEquals(1, count);
    }

    @Test
    void testCountByBookAndStatus() {
        // Act
        long count = bookCopyRepository.countByBookAndStatus(testBook, "AVAILABLE");

        // Assert
        assertEquals(1, count);
    }

    @Test
    void testCountByBookAndStatus_NoCopies() {
        // Act
        long count = bookCopyRepository.countByBookAndStatus(testBook, "BORROWED");

        // Assert
        assertEquals(0, count);
    }

    @Test
    void testCountByLibrary() {
        // Act
        long count = bookCopyRepository.countByLibrary(testLibrary);

        // Assert
        assertEquals(1, count);
    }

    @Test
    void testCountByLibraryAndStatus() {
        // Act
        long count = bookCopyRepository.countByLibraryAndStatus(testLibrary, "AVAILABLE");

        // Assert
        assertEquals(1, count);
    }
}
