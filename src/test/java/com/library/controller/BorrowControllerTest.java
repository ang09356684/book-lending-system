package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.request.BorrowRequest;
import com.library.entity.BorrowRecord;
import com.library.entity.BookCopy;
import com.library.entity.Book;
import com.library.entity.User;
import com.library.entity.Role;
import com.library.service.BorrowService;
import com.library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BorrowController Test
 * Tests REST API endpoints for borrowing management
 * 
 * @author Library System
 * @version 1.0.0
 */
@WebMvcTest(BorrowController.class)
class BorrowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowService borrowService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Book testBook;
    private BookCopy testBookCopy;
    private BorrowRecord testBorrowRecord;
    private Role memberRole;
    private BorrowRequest borrowRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        memberRole = new Role();
        memberRole.setId(1L);
        memberRole.setName("MEMBER");

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setRole(memberRole);

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");

        testBookCopy = new BookCopy();
        testBookCopy.setId(1L);
        testBookCopy.setBook(testBook);
        testBookCopy.setStatus("AVAILABLE");

        testBorrowRecord = new BorrowRecord();
        testBorrowRecord.setId(1L);
        testBorrowRecord.setUser(testUser);
        testBorrowRecord.setBookCopy(testBookCopy);
        testBorrowRecord.setBorrowedAt(LocalDateTime.now());
        testBorrowRecord.setDueAt(LocalDateTime.now().plusDays(30));
        testBorrowRecord.setStatus("BORROWED");

        borrowRequest = new BorrowRequest();
        borrowRequest.setUserId(1L);
        borrowRequest.setBookCopyId(1L);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "MEMBER")
    void testBorrowBook_Success() throws Exception {
        // Arrange
        when(userService.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(testUser));
        when(borrowService.borrowBook(1L, 1L)).thenReturn(testBorrowRecord);

        // Act & Assert
        mockMvc.perform(post("/borrowings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(borrowRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(borrowService).borrowBook(1L, 1L);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "MEMBER")
    void testBorrowBook_UserNotFound() throws Exception {
        // Arrange
        when(userService.findByEmail("john@example.com")).thenReturn(java.util.Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/borrowings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(borrowRequest)))
                .andExpect(status().isNotFound());

        verify(borrowService, never()).borrowBook(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "MEMBER")
    void testGetUserBorrows_Success() throws Exception {
        // Arrange
        when(userService.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(testUser));
        when(borrowService.getActiveBorrows(1L)).thenReturn(Arrays.asList(testBorrowRecord));

        // Act & Assert
        mockMvc.perform(get("/borrowings")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1));

        verify(borrowService).getActiveBorrows(1L);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "MEMBER")
    void testReturnBook_Success() throws Exception {
        // Arrange
        when(userService.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(testUser));
        when(borrowService.returnBook(1L)).thenReturn(testBorrowRecord);

        // Act & Assert
        mockMvc.perform(put("/borrowings/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(borrowService).returnBook(1L);
    }
}
