package com.library.repository;

import com.library.constant.BookType;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.entity.BorrowRecord;
import com.library.entity.Library;
import com.library.entity.Role;
import com.library.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BorrowRecordRepository Test
 * Tests database operations for BorrowRecord entity
 * 
 * @author Library System
 * @version 1.0.0
 */
@DataJpaTest
public class BorrowRecordRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    private User testUser;
    private Book testBook;
    private BookCopy testBookCopy;
    private BorrowRecord testBorrowRecord;
    private Role memberRole;
    private Library testLibrary;

    @BeforeEach
    void setUp() {
        // Setup test data
        memberRole = new Role();
        memberRole.setName("MEMBER");
        memberRole = entityManager.persistAndFlush(memberRole);

        testLibrary = new Library();
        testLibrary.setName("Test Library");
        testLibrary.setAddress("Test Address");
        testLibrary.setPhone("123-456-7890");
        testLibrary = entityManager.persistAndFlush(testLibrary);

        testUser = new User();
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("password123");
        testUser.setRole(memberRole);
        testUser.setIsVerified(false);
        testUser = entityManager.persistAndFlush(testUser);

        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setPublishedYear(2023);
        testBook.setCategory("Fiction");
        testBook.setBookType(BookType.TRADITIONAL);
        testBook = entityManager.persistAndFlush(testBook);

        testBookCopy = new BookCopy();
        testBookCopy.setBook(testBook);
        testBookCopy.setLibrary(testLibrary);
        testBookCopy.setCopyNumber(1);
        testBookCopy.setStatus("AVAILABLE");
        testBookCopy = entityManager.persistAndFlush(testBookCopy);

        testBorrowRecord = new BorrowRecord();
        testBorrowRecord.setUser(testUser);
        testBorrowRecord.setBookCopy(testBookCopy);
        testBorrowRecord.setBorrowedAt(LocalDateTime.now().minusDays(10));
        testBorrowRecord.setDueAt(LocalDateTime.now().plusDays(20));
        testBorrowRecord.setStatus("BORROWED");
        testBorrowRecord = entityManager.persistAndFlush(testBorrowRecord);
    }

    @Test
    void testFindByUser_Success() {
        // Act
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByUser(testUser);

        // Assert
        assertNotNull(borrowRecords);
        assertEquals(1, borrowRecords.size());
        assertEquals(testBorrowRecord.getId(), borrowRecords.get(0).getId());
        assertEquals(testUser.getId(), borrowRecords.get(0).getUser().getId());
    }

    @Test
    void testFindByUser_NoRecords() {
        // Arrange - Create a new user with no borrow records
        User newUser = new User();
        newUser.setName("Jane Smith");
        newUser.setEmail("jane@example.com");
        newUser.setPassword("password123");
        newUser.setRole(memberRole);
        newUser.setIsVerified(false);
        newUser = entityManager.persistAndFlush(newUser);

        // Act
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByUser(newUser);

        // Assert
        assertNotNull(borrowRecords);
        assertTrue(borrowRecords.isEmpty());
    }

    @Test
    void testFindByUserAndStatus_Success() {
        // Act
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByUserAndStatus(testUser, "BORROWED");

        // Assert
        assertNotNull(borrowRecords);
        assertEquals(1, borrowRecords.size());
        assertEquals("BORROWED", borrowRecords.get(0).getStatus());
    }

    @Test
    void testFindByUserAndStatus_NoRecords() {
        // Act
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByUserAndStatus(testUser, "RETURNED");

        // Assert
        assertNotNull(borrowRecords);
        assertTrue(borrowRecords.isEmpty());
    }

    @Test
    void testFindByBookCopy_Success() {
        // Act
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByBookCopy(testBookCopy);

        // Assert
        assertNotNull(borrowRecords);
        assertEquals(1, borrowRecords.size());
        assertEquals(testBookCopy.getId(), borrowRecords.get(0).getBookCopy().getId());
    }

    @Test
    void testFindByBookCopy_NoRecords() {
        // Arrange - Create a new book copy with no borrow records
        BookCopy newBookCopy = new BookCopy();
        newBookCopy.setBook(testBook);
        newBookCopy.setLibrary(testLibrary);
        newBookCopy.setCopyNumber(2);
        newBookCopy.setStatus("AVAILABLE");
        newBookCopy = entityManager.persistAndFlush(newBookCopy);

        // Act
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByBookCopy(newBookCopy);

        // Assert
        assertNotNull(borrowRecords);
        assertTrue(borrowRecords.isEmpty());
    }

    @Test
    void testFindByStatus_Success() {
        // Act
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByStatus("BORROWED");

        // Assert
        assertNotNull(borrowRecords);
        assertEquals(1, borrowRecords.size());
        assertEquals("BORROWED", borrowRecords.get(0).getStatus());
    }

    @Test
    void testFindByStatus_NoRecords() {
        // Act
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByStatus("RETURNED");

        // Assert
        assertNotNull(borrowRecords);
        assertTrue(borrowRecords.isEmpty());
    }

    @Test
    void testFindByStatusAndDueAtBetween_Success() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(30);

        // Act
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByStatusAndDueAtBetween("BORROWED", startDate, endDate);

        // Assert
        assertNotNull(borrowRecords);
        assertEquals(1, borrowRecords.size());
        assertEquals("BORROWED", borrowRecords.get(0).getStatus());
        assertTrue(borrowRecords.get(0).getDueAt().isAfter(startDate));
        assertTrue(borrowRecords.get(0).getDueAt().isBefore(endDate));
    }

    @Test
    void testFindByStatusAndDueAtBetween_NoRecords() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().plusDays(30);
        LocalDateTime endDate = LocalDateTime.now().plusDays(60);

        // Act
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByStatusAndDueAtBetween("BORROWED", startDate, endDate);

        // Assert
        assertNotNull(borrowRecords);
        assertTrue(borrowRecords.isEmpty());
    }

    @Test
    void testFindOverdueRecords_Success() {
        // Arrange - Create an overdue record
        BorrowRecord overdueRecord = new BorrowRecord();
        overdueRecord.setUser(testUser);
        overdueRecord.setBookCopy(testBookCopy);
        overdueRecord.setBorrowedAt(LocalDateTime.now().minusDays(40));
        overdueRecord.setDueAt(LocalDateTime.now().minusDays(10)); // Overdue
        overdueRecord.setStatus("BORROWED");
        entityManager.persistAndFlush(overdueRecord);

        // Act
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords(testUser.getId(), "BORROWED", LocalDateTime.now());

        // Assert
        assertNotNull(overdueRecords);
        assertEquals(1, overdueRecords.size());
        assertTrue(overdueRecords.get(0).getDueAt().isBefore(LocalDateTime.now()));
    }

    @Test
    void testFindOverdueRecords_NoOverdueRecords() {
        // Act
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords(testUser.getId(), "BORROWED", LocalDateTime.now());

        // Assert
        assertNotNull(overdueRecords);
        assertTrue(overdueRecords.isEmpty());
    }

    @Test
    void testSave_NewBorrowRecord() {
        // Arrange
        BorrowRecord newBorrowRecord = new BorrowRecord();
        newBorrowRecord.setUser(testUser);
        newBorrowRecord.setBookCopy(testBookCopy);
        newBorrowRecord.setBorrowedAt(LocalDateTime.now());
        newBorrowRecord.setDueAt(LocalDateTime.now().plusDays(30));
        newBorrowRecord.setStatus("BORROWED");

        // Act
        BorrowRecord savedRecord = borrowRecordRepository.save(newBorrowRecord);

        // Assert
        assertNotNull(savedRecord);
        assertNotNull(savedRecord.getId());
        assertEquals(testUser.getId(), savedRecord.getUser().getId());
        assertEquals(testBookCopy.getId(), savedRecord.getBookCopy().getId());
        assertEquals("BORROWED", savedRecord.getStatus());
    }

    @Test
    void testSave_UpdateBorrowRecord() {
        // Arrange
        testBorrowRecord.setStatus("RETURNED");
        testBorrowRecord.setReturnedAt(LocalDateTime.now());

        // Act
        BorrowRecord updatedRecord = borrowRecordRepository.save(testBorrowRecord);

        // Assert
        assertNotNull(updatedRecord);
        assertEquals("RETURNED", updatedRecord.getStatus());
        assertNotNull(updatedRecord.getReturnedAt());
    }

    @Test
    void testFindById_Success() {
        // Act
        Optional<BorrowRecord> foundRecord = borrowRecordRepository.findById(testBorrowRecord.getId());

        // Assert
        assertTrue(foundRecord.isPresent());
        assertEquals(testBorrowRecord.getId(), foundRecord.get().getId());
        assertEquals(testUser.getId(), foundRecord.get().getUser().getId());
    }

    @Test
    void testFindById_NotFound() {
        // Act
        Optional<BorrowRecord> foundRecord = borrowRecordRepository.findById(999L);

        // Assert
        assertFalse(foundRecord.isPresent());
    }

    @Test
    void testCountByUserAndStatus() {
        // Act
        long count = borrowRecordRepository.countByUserAndStatus(testUser, "BORROWED");

        // Assert
        assertEquals(1, count);
    }

    @Test
    void testCountByUserAndStatus_NoRecords() {
        // Act
        long count = borrowRecordRepository.countByUserAndStatus(testUser, "RETURNED");

        // Assert
        assertEquals(0, count);
    }

    @Test
    void testCountByBookCopy() {
        // Act
        long count = borrowRecordRepository.countByBookCopy(testBookCopy);

        // Assert
        assertEquals(1, count);
    }

    @Test
    void testCountByStatus() {
        // Act
        long count = borrowRecordRepository.countByStatus("BORROWED");

        // Assert
        assertEquals(1, count);
    }
}
