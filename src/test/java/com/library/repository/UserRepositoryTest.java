package com.library.repository;

import com.library.entity.Role;
import com.library.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserRepository Integration Test
 * Uses H2 in-memory database for real database operation testing
 * 
 * @author Library System
 * @version 1.0.0
 */
@ActiveProfiles("test")
public class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role memberRole;
    private Role librarianRole;
    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // Clean up database
        entityManager.clear();

        // Create test roles
        memberRole = new Role();
        memberRole.setName("MEMBER");
        memberRole = entityManager.persistAndFlush(memberRole);

        librarianRole = new Role();
        librarianRole.setName("LIBRARIAN");
        librarianRole = entityManager.persistAndFlush(librarianRole);

        // Create test users
        testUser1 = new User();
        testUser1.setName("John Doe");
        testUser1.setEmail("john@example.com");
        testUser1.setPassword("encodedPassword1");
        testUser1.setRole(memberRole);
        testUser1.setIsVerified(false);
        testUser1 = entityManager.persistAndFlush(testUser1);

        testUser2 = new User();
        testUser2.setName("Jane Smith");
        testUser2.setEmail("jane@example.com");
        testUser2.setPassword("encodedPassword2");
        testUser2.setRole(librarianRole);
        testUser2.setIsVerified(true);
        testUser2.setLibrarianId("LIB001");
        testUser2 = entityManager.persistAndFlush(testUser2);

        entityManager.flush();
    }

    @Test
    void testFindByEmail_Success() {
        // Act
        Optional<User> result = userRepository.findByEmail("john@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        assertEquals("john@example.com", result.get().getEmail());
        assertEquals("MEMBER", result.get().getRole().getName());
    }

    @Test
    void testFindByEmail_NotFound() {
        // Act
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByEmailWithRole_Success() {
        // Act
        Optional<User> result = userRepository.findByEmailWithRole("jane@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Jane Smith", result.get().getName());
        assertEquals("LIBRARIAN", result.get().getRole().getName());
        assertTrue(result.get().getIsVerified());
    }

    @Test
    void testFindById_Success() {
        // Act
        Optional<User> result = userRepository.findById(testUser1.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        assertEquals("john@example.com", result.get().getEmail());
    }

    @Test
    void testFindById_NotFound() {
        // Act
        Optional<User> result = userRepository.findById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByRole_Success() {
        // Act
        List<User> memberUsers = userRepository.findByRole(memberRole);
        List<User> librarianUsers = userRepository.findByRole(librarianRole);

        // Assert
        assertEquals(1, memberUsers.size());
        assertEquals("John Doe", memberUsers.get(0).getName());
        assertEquals("MEMBER", memberUsers.get(0).getRole().getName());

        assertEquals(1, librarianUsers.size());
        assertEquals("Jane Smith", librarianUsers.get(0).getName());
        assertEquals("LIBRARIAN", librarianUsers.get(0).getRole().getName());
    }

    @Test
    void testExistsByEmail_True() {
        // Act
        boolean exists = userRepository.existsByEmail("john@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_False() {
        // Act
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(exists);
    }

    @Test
    void testSave_NewUser() {
        // Arrange
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@example.com");
        newUser.setPassword("encodedPassword");
        newUser.setRole(memberRole);
        newUser.setIsVerified(false);

        // Act
        User savedUser = userRepository.save(newUser);

        // Assert
        assertNotNull(savedUser.getId());
        assertEquals("New User", savedUser.getName());
        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals("MEMBER", savedUser.getRole().getName());
        assertFalse(savedUser.getIsVerified());
    }

    @Test
    void testSave_UpdateUser() {
        // Arrange
        testUser1.setName("Updated Name");
        testUser1.setIsVerified(true);

        // Act
        User updatedUser = userRepository.save(testUser1);

        // Assert
        assertEquals("Updated Name", updatedUser.getName());
        assertTrue(updatedUser.getIsVerified());
        
        // Verify that data in database is actually updated
        Optional<User> foundUser = userRepository.findById(testUser1.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Updated Name", foundUser.get().getName());
        assertTrue(foundUser.get().getIsVerified());
    }

    @Test
    void testFindVerifiedUsersByRole() {
        // Act
        List<User> verifiedLibrarians = userRepository.findVerifiedUsersByRole(librarianRole, true);
        List<User> verifiedMembers = userRepository.findVerifiedUsersByRole(memberRole, true);

        // Assert
        assertEquals(1, verifiedLibrarians.size());
        assertEquals("Jane Smith", verifiedLibrarians.get(0).getName());
        assertTrue(verifiedLibrarians.get(0).getIsVerified());

        assertEquals(0, verifiedMembers.size()); // testUser1 is not verified
    }

    @Test
    void testDeleteUser() {
        // Arrange
        Long userId = testUser1.getId();

        // Act
        userRepository.deleteById(userId);

        // Assert
        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }
}
