package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.request.UpdateUserRequest;
import com.library.entity.Role;
import com.library.entity.User;
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
 * UserController Test
 * Tests REST API endpoints for user management
 * 
 * @author Library System
 * @version 1.0.0
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Role memberRole;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        memberRole = new Role();
        memberRole.setId(1L);
        memberRole.setName("MEMBER");

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setRole(memberRole);
        testUser.setIsVerified(false);

        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Updated John Doe");
        updateUserRequest.setEmail("updated.john@example.com");
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "MEMBER")
    void testGetCurrentUser_Success() throws Exception {
        // Arrange
        when(userService.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(get("/users/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));

        verify(userService).findByEmail("john@example.com");
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "MEMBER")
    void testGetCurrentUser_UserNotFound() throws Exception {
        // Arrange
        when(userService.findByEmail("john@example.com")).thenReturn(java.util.Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/users/current"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        verify(userService).findByEmail("john@example.com");
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "MEMBER")
    void testGetUserById_Success() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("John Doe"));

        verify(userService).findById(1L);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "MEMBER")
    void testGetUserById_NotFound() throws Exception {
        // Arrange
        when(userService.findById(999L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        verify(userService).findById(999L);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "MEMBER")
    void testUpdateUser_Success() throws Exception {
        // Arrange
        when(userService.updateUser(eq(1L), anyString(), anyString(), anyString()))
            .thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(userService).updateUser(
            1L,
            updateUserRequest.getName(),
            updateUserRequest.getEmail(),
            null
        );
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "MEMBER")
    void testUpdateUser_InvalidRequest() throws Exception {
        // Arrange
        UpdateUserRequest invalidRequest = new UpdateUserRequest();
        invalidRequest.setName(""); // Invalid empty name

        // Act & Assert
        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "MEMBER")
    void testUpdateUser_AccessDenied() throws Exception {
        // Act & Assert - User trying to update another user's data
        mockMvc.perform(put("/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isForbidden());

        verify(userService, never()).updateUser(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "librarian@library.com", roles = "LIBRARIAN")
    void testUpdateUser_LibrarianCanUpdateAnyUser() throws Exception {
        // Arrange
        when(userService.updateUser(eq(1L), anyString(), anyString(), anyString()))
            .thenReturn(testUser);

        // Act & Assert - Librarian can update any user
        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userService).updateUser(
            1L,
            updateUserRequest.getName(),
            updateUserRequest.getEmail(),
            null
        );
    }

    @Test
    @WithMockUser(username = "librarian@library.com", roles = "LIBRARIAN")
    void testGetUsersByRole_Success() throws Exception {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userService.findByRole(any(Role.class))).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/users/role/MEMBER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("John Doe"));

        verify(userService).findByRole(any(Role.class));
    }

    @Test
    @WithMockUser(username = "member@example.com", roles = "MEMBER")
    void testGetUsersByRole_AccessDenied() throws Exception {
        // Act & Assert - MEMBER role cannot access this endpoint
        mockMvc.perform(get("/users/role/MEMBER"))
                .andExpect(status().isForbidden());

        verify(userService, never()).findByRole(any(Role.class));
    }

    @Test
    @WithMockUser(username = "librarian@library.com", roles = "LIBRARIAN")
    void testGetUserByEmail_Success() throws Exception {
        // Arrange
        when(userService.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(get("/users/email/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));

        verify(userService).findByEmail("john@example.com");
    }

    @Test
    @WithMockUser(username = "member@example.com", roles = "MEMBER")
    void testGetUserByEmail_AccessDenied() throws Exception {
        // Act & Assert - MEMBER role cannot access this endpoint
        mockMvc.perform(get("/users/email/john@example.com"))
                .andExpect(status().isForbidden());

        verify(userService, never()).findByEmail(anyString());
    }

    @Test
    @WithMockUser(username = "librarian@library.com", roles = "LIBRARIAN")
    void testUpdateUserVerification_Success() throws Exception {
        // Arrange
        when(userService.updateVerificationStatus(1L, true)).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(put("/users/1/verification")
                .param("verified", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(userService).updateVerificationStatus(1L, true);
    }

    @Test
    @WithMockUser(username = "member@example.com", roles = "MEMBER")
    void testUpdateUserVerification_AccessDenied() throws Exception {
        // Act & Assert - MEMBER role cannot access this endpoint
        mockMvc.perform(put("/users/1/verification")
                .param("verified", "true"))
                .andExpect(status().isForbidden());

        verify(userService, never()).updateVerificationStatus(anyLong(), anyBoolean());
    }


}
