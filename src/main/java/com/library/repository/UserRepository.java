package com.library.repository;

import com.library.entity.Role;
import com.library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User Repository - Data access layer for User entity
 * 
 * @author Library System
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Basic query methods
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by email with role loaded
     */
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    Optional<User> findByEmailWithRole(@Param("email") String email);
    
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.role = :role")
    List<User> findByRole(@Param("role") Role role);
    boolean existsByEmail(String email);
    
    // Complex queries
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isVerified = :verified")
    List<User> findVerifiedUsersByRole(@Param("role") Role role, @Param("verified") Boolean verified);
    
    // Search functionality
    @Query(value = "SELECT * FROM users u JOIN roles r ON u.role_id = r.id WHERE " +
           "(:name IS NULL OR u.name ILIKE '%' || :name || '%') AND " +
           "(:email IS NULL OR u.email ILIKE '%' || :email || '%') AND " +
           "(:role IS NULL OR r.name = :role)", 
           nativeQuery = true)
    List<User> searchUsers(@Param("name") String name, 
                           @Param("email") String email, 
                           @Param("role") String role);
    
    // Pagination queries
    Page<User> findByRole(Role role, Pageable pageable);
    
    // Statistics queries
    long countByRole(Role role);
    long countByRoleAndIsVerified(Role role, Boolean isVerified);
}
