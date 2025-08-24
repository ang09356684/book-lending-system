package com.library.service;

import com.library.constant.BookType;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.repository.BookCopyRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BorrowService
 * Testing core borrowing and returning business logic
 */
@ExtendWith(MockitoExtension.class)
public class BorrowServiceTest {

    @Mock
    private BorrowRecordRepository borrowRecordRepository;
    
    @Mock
    private BookCopyRepository bookCopyRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private BorrowService borrowService;
    
    private User testUser;
    private BookCopy testBookCopy;
    private Book testBook;
    private BorrowRecord testBorrowRecord;
    
    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        
        // Create test book
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setBookType(BookType.TRADITIONAL);
        
        // Create test book copy
        testBookCopy = new BookCopy();
        testBookCopy.setId(1L);
        testBookCopy.setBook(testBook);
        testBookCopy.setStatus("AVAILABLE");
        
        // Create test borrow record
        testBorrowRecord = new BorrowRecord();
        testBorrowRecord.setId(1L);
        testBorrowRecord.setUser(testUser);
        testBorrowRecord.setBookCopy(testBookCopy);
        testBorrowRecord.setBorrowedAt(LocalDateTime.now());
        testBorrowRecord.setDueAt(LocalDateTime.now().plusDays(30));
        testBorrowRecord.setStatus("BORROWED");
        
        // Reset mocks
        reset(borrowRecordRepository, bookCopyRepository, userRepository);
    }
    
    @Test
    @DisplayName("Test borrow book - Success")
    void testBorrowBook_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(testBookCopy));
        when(borrowRecordRepository.findByUserAndStatus(testUser, "BORROWED")).thenReturn(Arrays.asList());
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(testBorrowRecord);
        when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(testBookCopy);
        
        // Act
        BorrowRecord result = borrowService.borrowBook(1L, 1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(testBookCopy, result.getBookCopy());
        
        // Verify interactions
        verify(userRepository).findById(1L);
        verify(bookCopyRepository).findById(1L);
        verify(borrowRecordRepository).findByUserAndStatus(testUser, "BORROWED");
        verify(borrowRecordRepository).save(any(BorrowRecord.class));
        verify(bookCopyRepository).save(any(BookCopy.class));
    }
    
    @Test
    @DisplayName("Test borrow book - User not found")
    void testBorrowBook_UserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowService.borrowBook(999L, 1L);
        });
        
        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(999L);
        verifyNoInteractions(bookCopyRepository, borrowRecordRepository);
    }
    
    @Test
    @DisplayName("Test borrow book - Book copy not found")
    void testBorrowBook_BookCopyNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookCopyRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowService.borrowBook(1L, 999L);
        });
        
        assertEquals("Book copy not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(bookCopyRepository).findById(999L);
        verifyNoInteractions(borrowRecordRepository);
    }
    
    @Test
    @DisplayName("Test borrow book - Book not available")
    void testBorrowBook_BookNotAvailable() {
        // Arrange
        testBookCopy.setStatus("BORROWED");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(testBookCopy));
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowService.borrowBook(1L, 1L);
        });
        
        assertEquals("Book is not available", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(bookCopyRepository).findById(1L);
        verifyNoInteractions(borrowRecordRepository);
    }
    
    @Test
    @DisplayName("Test return book - Success")
    void testReturnBook_Success() {
        // Arrange
        when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(testBorrowRecord));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(testBorrowRecord);
        when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(testBookCopy);
        
        // Act
        BorrowRecord result = borrowService.returnBook(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals("RETURNED", result.getStatus());
        assertNotNull(result.getReturnedAt());
        
        // Verify interactions
        verify(borrowRecordRepository).findById(1L);
        verify(borrowRecordRepository).save(any(BorrowRecord.class));
        verify(bookCopyRepository).save(any(BookCopy.class));
    }
    
    @Test
    @DisplayName("Test return book - Borrow record not found")
    void testReturnBook_NotFound() {
        // Arrange
        when(borrowRecordRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowService.returnBook(999L);
        });
        
        assertEquals("Borrow record not found", exception.getMessage());
        verify(borrowRecordRepository).findById(999L);
        verifyNoMoreInteractions(borrowRecordRepository);
        verifyNoInteractions(bookCopyRepository);
    }
    
    @Test
    @DisplayName("Test get user borrow records")
    void testGetUserBorrowRecords_Success() {
        // Arrange
        List<BorrowRecord> expectedRecords = Arrays.asList(testBorrowRecord);
        when(borrowRecordRepository.findByUser(testUser)).thenReturn(expectedRecords);
        
        // Act
        List<BorrowRecord> result = borrowService.findByUser(testUser);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBorrowRecord, result.get(0));
        
        // Verify interactions
        verify(borrowRecordRepository).findByUser(testUser);
    }
    
    @Test
    @DisplayName("Test count active borrows")
    void testCountActiveBorrows_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(borrowRecordRepository.countByUserAndStatus(testUser, "BORROWED")).thenReturn(3L);
        
        // Act
        long result = borrowService.countActiveBorrows(1L);
        
        // Assert
        assertEquals(3L, result);
        verify(userRepository).findById(1L);
        verify(borrowRecordRepository).countByUserAndStatus(testUser, "BORROWED");
    }
    
    @Test
    @DisplayName("Test get active borrows")
    void testGetActiveBorrows_Success() {
        // Arrange
        List<BorrowRecord> expectedRecords = Arrays.asList(testBorrowRecord);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(borrowRecordRepository.findByUserAndStatus(testUser, "BORROWED")).thenReturn(expectedRecords);
        
        // Act
        List<BorrowRecord> result = borrowService.getActiveBorrows(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBorrowRecord, result.get(0));
        
        // Verify interactions
        verify(userRepository).findById(1L);
        verify(borrowRecordRepository).findByUserAndStatus(testUser, "BORROWED");
    }
    
    @Test
    @DisplayName("Test has overdue books - True")
    void testHasOverdueBooks_True() {
        // Arrange
        List<BorrowRecord> overdueRecords = Arrays.asList(testBorrowRecord);
        when(borrowRecordRepository.findOverdueRecords(eq(1L), eq("BORROWED"), any(LocalDateTime.class)))
            .thenReturn(overdueRecords);
        
        // Act
        boolean result = borrowService.hasOverdueBooks(1L);
        
        // Assert
        assertTrue(result);
        verify(borrowRecordRepository).findOverdueRecords(eq(1L), eq("BORROWED"), any(LocalDateTime.class));
    }
    
    @Test
    @DisplayName("Test has overdue books - False")
    void testHasOverdueBooks_False() {
        // Arrange
        when(borrowRecordRepository.findOverdueRecords(eq(1L), eq("BORROWED"), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList());
        
        // Act
        boolean result = borrowService.hasOverdueBooks(1L);
        
        // Assert
        assertFalse(result);
        verify(borrowRecordRepository).findOverdueRecords(eq(1L), eq("BORROWED"), any(LocalDateTime.class));
    }
}