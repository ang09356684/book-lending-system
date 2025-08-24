package com.library.service;

import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.RoleRepository;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService Unit Test
 * 
 * @author Library System
 * @version 1.0.0
 */
@SpringBootTest
public class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @Autowired
    private ExternalApiService externalApiService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    private User testUser;
    private Role memberRole;
    private Role librarianRole;

    @BeforeEach
    void setUp() {
        // Create test data
        memberRole = new Role();
        memberRole.setId(1L);
        memberRole.setName("MEMBER");

        librarianRole = new Role();
        librarianRole.setId(2L);
        librarianRole.setName("LIBRARIAN");

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(memberRole);
        testUser.setIsVerified(false);
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        String name = "Jane Smith";
        String email = "jane@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(roleRepository.findByName("MEMBER")).thenReturn(Optional.of(memberRole));
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        
        // Create a new user with the expected values
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setName(name);
        expectedUser.setEmail(email);
        expectedUser.setPassword(encodedPassword);
        expectedUser.setRole(memberRole);
        expectedUser.setIsVerified(false);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        // Act
        User result = userService.registerUser(name, email, password);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        assertEquals(encodedPassword, result.getPassword());
        assertEquals(memberRole, result.getRole());
        assertFalse(result.getIsVerified());

        verify(userRepository).existsByEmail(email);
        verify(roleRepository).findByName("MEMBER");
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Arrange
        String email = "existing@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser("Test User", email, "password");
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterLibrarian_Success() {
        // Arrange
        String name = "Librarian Admin";
        String email = "librarian@library.com";
        String password = "password123";
        String librarianId = "LIB001";
        String encodedPassword = "encodedPassword123";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(roleRepository.findByName("LIBRARIAN")).thenReturn(Optional.of(librarianRole));
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        // externalApiService is not a Mock, use real logic
        
        // Create a new user with the expected values
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setName(name);
        expectedUser.setEmail(email);
        expectedUser.setPassword(encodedPassword);
        expectedUser.setRole(librarianRole);
        expectedUser.setLibrarianId(librarianId);
        expectedUser.setIsVerified(true);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        // Act
        User result = userService.registerLibrarian(name, email, password, librarianId);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        assertEquals(encodedPassword, result.getPassword());
        assertEquals(librarianRole, result.getRole());
        assertEquals(librarianId, result.getLibrarianId());
        assertTrue(result.getIsVerified());

        verify(userRepository).existsByEmail(email);
        verify(roleRepository).findByName("LIBRARIAN");
        verify(passwordEncoder).encode(password);
        // externalApiService is not a Mock, so don't verify
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testFindByEmail_Success() {
        // Arrange
        String email = "john@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.findByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testFindByEmail_NotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByEmail(email);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testFindByEmailWithRole_Success() {
        // Arrange
        String email = "john@example.com";
        when(userRepository.findByEmailWithRole(email)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.findByEmailWithRole(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository).findByEmailWithRole(email);
    }

    @Test
    void testFindById_Success() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.findById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(userId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void testFindByRole_Success() {
        // Arrange
        List<User> users = List.of(testUser);
        when(userRepository.findByRole(memberRole)).thenReturn(users);

        // Act
        List<User> result = userService.findByRole(memberRole);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
        verify(userRepository).findByRole(memberRole);
    }

    @Test
    void testUpdateVerificationStatus_Success() {
        // Arrange
        Long userId = 1L;
        boolean verified = true;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.updateVerificationStatus(userId, verified);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsVerified());
        verify(userRepository).findById(userId);
        verify(userRepository).save(testUser);
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        Long userId = 1L;
        String newName = "Updated Name";
        String newEmail = "updated@example.com";
        String newPassword = "newPassword";
        String encodedPassword = "encodedNewPassword";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.updateUser(userId, newName, newEmail, newPassword);

        // Assert
        assertNotNull(result);
        assertEquals(newName, result.getName());
        assertEquals(newEmail, result.getEmail());
        assertEquals(encodedPassword, result.getPassword());
        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(testUser);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userId, "New Name", "new@example.com", "password");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }
}
