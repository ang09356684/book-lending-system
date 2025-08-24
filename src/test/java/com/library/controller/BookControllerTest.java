package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.constant.BookType;
import com.library.dto.request.CreateBookRequest;
import com.library.dto.request.UpdateBookRequest;
import com.library.entity.Book;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.service.BookService;
import com.library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BookController Test
 * Tests REST API endpoints
 * 
 * @author Library System
 * @version 1.0.0
 */
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book testBook;
    private CreateBookRequest createBookRequest;
    private UpdateBookRequest updateBookRequest;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setPublishedYear(2023);
        testBook.setCategory("Fiction");
        testBook.setBookType(BookType.TRADITIONAL);

        createBookRequest = new CreateBookRequest();
        createBookRequest.setTitle("New Book");
        createBookRequest.setAuthor("New Author");
        createBookRequest.setPublishedYear(2024);
        createBookRequest.setCategory("Science Fiction");
        createBookRequest.setBookType(BookType.TRADITIONAL);

        updateBookRequest = new UpdateBookRequest();
        updateBookRequest.setTitle("Updated Book");
        updateBookRequest.setAuthor("Updated Author");
        updateBookRequest.setPublishedYear(2025);
        updateBookRequest.setCategory("Updated Category");
        updateBookRequest.setBookType(BookType.MODERN);
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testCreateBook_Success() throws Exception {
        // Arrange
        when(bookService.createBook(anyString(), anyString(), anyInt(), anyString(), anyString()))
            .thenReturn(testBook);

        // Act & Assert
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBookRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Book"))
                .andExpect(jsonPath("$.data.author").value("Test Author"));

        verify(bookService).createBook(
            createBookRequest.getTitle(),
            createBookRequest.getAuthor(),
            createBookRequest.getPublishedYear(),
            createBookRequest.getCategory(),
            createBookRequest.getBookType()
        );
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testCreateBook_InvalidInput() throws Exception {
        // Arrange - Create invalid request
        CreateBookRequest invalidRequest = new CreateBookRequest();
        // 不設定必要欄位，應該會觸發驗證錯誤

        // Act & Assert
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void testCreateBook_AccessDenied() throws Exception {
        // Arrange
        when(bookService.createBook(anyString(), anyString(), anyInt(), anyString(), anyString()))
            .thenReturn(testBook);

        // Act & Assert - 非館員應該被拒絕存取
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBookRequest)))
                .andExpect(status().isForbidden());
    }



    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testGetBook_Success() throws Exception {
        // Arrange
        when(bookService.findById(1L)).thenReturn(testBook);

        // Act & Assert
        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Book"));

        verify(bookService).findById(1L);
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testGetBook_NotFound() throws Exception {
        // Arrange
        when(bookService.findById(999L)).thenThrow(new RuntimeException("Book not found"));

        // Act & Assert
        mockMvc.perform(get("/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Book not found"));

        verify(bookService).findById(999L);
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testSearchBooks_Success() throws Exception {
        // Arrange
        List<Book> books = Arrays.asList(testBook);
        when(bookService.searchBooks(anyString(), anyString(), anyInt()))
            .thenReturn(books);

        // Act & Assert
        mockMvc.perform(get("/books/search")
                .param("title", "Test")
                .param("author", "Author")
                .param("publishedYear", "2023"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("Test Book"));

        verify(bookService).searchBooks("Test", "Author", 2023);
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testSearchBooks_NoParameters() throws Exception {
        // Arrange
        List<Book> books = Arrays.asList(testBook);
        when(bookService.searchBooks(null, null, null))
            .thenReturn(books);

        // Act & Assert
        mockMvc.perform(get("/books/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1));

        verify(bookService).searchBooks(null, null, null);
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testUpdateBook_Success() throws Exception {
        // Arrange
        when(bookService.updateBook(anyLong(), anyString(), anyString(), anyInt(), anyString(), anyString()))
            .thenReturn(testBook);

        // Act & Assert
        mockMvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(bookService).updateBook(
            1L,
            updateBookRequest.getTitle(),
            updateBookRequest.getAuthor(),
            updateBookRequest.getPublishedYear(),
            updateBookRequest.getCategory(),
            updateBookRequest.getBookType()
        );
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void testUpdateBook_AccessDenied() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBookRequest)))
                .andExpect(status().isForbidden());
    }



    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testGetAllBooks_Success() throws Exception {
        // Arrange
        List<Book> books = Arrays.asList(testBook);
        when(bookService.getAllBooks(0, 10)).thenReturn(books);

        // Act & Assert
        mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1));

        verify(bookService).getAllBooks(0, 10);
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testGetAllBooks_DefaultPagination() throws Exception {
        // Arrange
        List<Book> books = Arrays.asList(testBook);
        when(bookService.getAllBooks(0, 20)).thenReturn(books);

        // Act & Assert
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

        verify(bookService).getAllBooks(0, 20);
    }
}
