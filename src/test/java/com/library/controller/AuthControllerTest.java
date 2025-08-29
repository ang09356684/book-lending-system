package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.request.LoginRequest;
import com.library.dto.request.RegisterRequest;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.security.JwtTokenProvider;
import com.library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController Test
 * Tests authentication-related REST API endpoints
 * 
 * @author Library System
 * @version 1.0.0
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Role memberRole;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

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
        testUser.setPassword("encodedPassword");
        testUser.setRole(memberRole);
        testUser.setIsVerified(false);

        registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // Arrange
        when(userService.registerUser(anyString(), anyString(), anyString()))
            .thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));

        verify(userService).registerUser(
            registerRequest.getName(),
            registerRequest.getEmail(),
            registerRequest.getPassword()
        );
    }

    @Test
    void testRegisterUser_InvalidRequest() throws Exception {
        // Arrange - Create invalid request
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setName(""); // Empty name
        invalidRequest.setEmail("invalid-email"); // Invalid email
        invalidRequest.setPassword("123"); // Too short password

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterUser_DuplicateEmail() throws Exception {
        // Arrange
        when(userService.registerUser(anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("Email already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already exists"));

        verify(userService).registerUser(
            registerRequest.getName(),
            registerRequest.getEmail(),
            registerRequest.getPassword()
        );
    }

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        when(userService.findByEmail(loginRequest.getEmail()))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString()))
            .thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.user.id").value(1))
                .andExpect(jsonPath("$.data.user.email").value("john@example.com"));

        verify(userService).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPassword());
    }

    @Test
    void testLogin_UserNotFound() throws Exception {
        // Arrange
        when(userService.findByEmail(loginRequest.getEmail()))
            .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));

        verify(userService).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testLogin_InvalidPassword() throws Exception {
        // Arrange
        when(userService.findByEmail(loginRequest.getEmail()))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString()))
            .thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));

        verify(userService).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPassword());
    }

    @Test
    void testLogin_InvalidRequest() throws Exception {
        // Arrange - Create invalid request
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setPassword(""); // Empty password

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterLibrarian_Success() throws Exception {
        // Arrange
        RegisterRequest librarianRequest = new RegisterRequest();
        librarianRequest.setName("Librarian Admin");
        librarianRequest.setEmail("librarian@library.com");
        librarianRequest.setPassword("password123");

        User librarianUser = new User();
        librarianUser.setId(2L);
        librarianUser.setName("Librarian Admin");
        librarianUser.setEmail("librarian@library.com");
        librarianUser.setLibrarianId("LIB001");
        librarianUser.setIsVerified(true);

        when(userService.registerLibrarian(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(librarianUser);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register/librarian")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(librarianRequest))
                .param("librarianId", "LIB001"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.name").value("Librarian Admin"))
                .andExpect(jsonPath("$.data.librarianId").value("LIB001"))
                .andExpect(jsonPath("$.data.isVerified").value(true));

        verify(userService).registerLibrarian(
            librarianRequest.getName(),
            librarianRequest.getEmail(),
            librarianRequest.getPassword(),
            "LIB001"
        );
    }

    @Test
    void testRegisterLibrarian_InvalidLibrarianId() throws Exception {
        // Arrange
        when(userService.registerLibrarian(anyString(), anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("Invalid librarian ID"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register/librarian")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .param("librarianId", "INVALID_ID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid librarian ID"));

        verify(userService).registerLibrarian(
            registerRequest.getName(),
            registerRequest.getEmail(),
            registerRequest.getPassword(),
            "INVALID_ID"
        );
    }
}
