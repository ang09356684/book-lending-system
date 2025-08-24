package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.request.CreateLibraryRequest;
import com.library.dto.request.UpdateLibraryRequest;
import com.library.entity.Library;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.service.LibraryService;
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
 * LibraryController Test
 * Tests REST API endpoints for library management
 * 
 * @author Library System
 * @version 1.0.0
 */
@WebMvcTest(LibraryController.class)
class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibraryService libraryService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Library testLibrary;
    private CreateLibraryRequest createLibraryRequest;
    private UpdateLibraryRequest updateLibraryRequest;

    @BeforeEach
    void setUp() {
        testLibrary = new Library();
        testLibrary.setId(1L);
        testLibrary.setName("Test Library");
        testLibrary.setAddress("Test Address");
        testLibrary.setPhone("123-456-7890");

        createLibraryRequest = new CreateLibraryRequest();
        createLibraryRequest.setName("New Library");
        createLibraryRequest.setAddress("New Address");
        createLibraryRequest.setPhone("987-654-3210");

        updateLibraryRequest = new UpdateLibraryRequest();
        updateLibraryRequest.setName("Updated Library");
        updateLibraryRequest.setAddress("Updated Address");
        updateLibraryRequest.setPhone("555-555-5555");
    }

    @Test
    @WithMockUser(username = "librarian@library.com", roles = "LIBRARIAN")
    void testCreateLibrary_Success() throws Exception {
        // Arrange
        when(libraryService.createLibrary(anyString(), anyString(), anyString())).thenReturn(testLibrary);

        // Act & Assert
        mockMvc.perform(post("/libraries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createLibraryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Library"));

        verify(libraryService).createLibrary(
            createLibraryRequest.getName(),
            createLibraryRequest.getAddress(),
            createLibraryRequest.getPhone()
        );
    }

    @Test
    @WithMockUser(username = "member@example.com", roles = "MEMBER")
    void testCreateLibrary_AccessDenied() throws Exception {
        // Act & Assert - MEMBER role cannot access this endpoint
        mockMvc.perform(post("/libraries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createLibraryRequest)))
                .andExpect(status().isForbidden());

        verify(libraryService, never()).createLibrary(anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "librarian@library.com", roles = "LIBRARIAN")
    void testGetAllLibraries_Success() throws Exception {
        // Arrange
        List<Library> libraries = Arrays.asList(testLibrary);
        when(libraryService.findAll()).thenReturn(libraries);

        // Act & Assert
        mockMvc.perform(get("/libraries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Test Library"));

        verify(libraryService).findAll();
    }

    @Test
    @WithMockUser(username = "librarian@library.com", roles = "LIBRARIAN")
    void testGetLibraryById_Success() throws Exception {
        // Arrange
        when(libraryService.findById(1L)).thenReturn(testLibrary);

        // Act & Assert
        mockMvc.perform(get("/libraries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Library"));

        verify(libraryService).findById(1L);
    }

    @Test
    @WithMockUser(username = "librarian@library.com", roles = "LIBRARIAN")
    void testUpdateLibrary_Success() throws Exception {
        // Arrange
        when(libraryService.updateLibrary(eq(1L), anyString(), anyString(), anyString())).thenReturn(testLibrary);

        // Act & Assert
        mockMvc.perform(put("/libraries/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLibraryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(libraryService).updateLibrary(
            1L,
            updateLibraryRequest.getName(),
            updateLibraryRequest.getAddress(),
            updateLibraryRequest.getPhone()
        );
    }

    @Test
    @WithMockUser(username = "member@example.com", roles = "MEMBER")
    void testUpdateLibrary_AccessDenied() throws Exception {
        // Act & Assert - MEMBER role cannot access this endpoint
        mockMvc.perform(put("/libraries/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLibraryRequest)))
                .andExpect(status().isForbidden());

        verify(libraryService, never()).updateLibrary(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "librarian@library.com", roles = "LIBRARIAN")
    void testDeleteLibrary_Success() throws Exception {
        // Arrange
        doNothing().when(libraryService).deleteLibrary(1L);

        // Act & Assert
        mockMvc.perform(delete("/libraries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Library deleted successfully"));

        verify(libraryService).deleteLibrary(1L);
    }

    @Test
    @WithMockUser(username = "member@example.com", roles = "MEMBER")
    void testDeleteLibrary_AccessDenied() throws Exception {
        // Act & Assert - MEMBER role cannot access this endpoint
        mockMvc.perform(delete("/libraries/1"))
                .andExpect(status().isForbidden());

        verify(libraryService, never()).deleteLibrary(anyLong());
    }
}
