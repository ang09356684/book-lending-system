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
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // Complex queries
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isVerified = :verified")
    List<User> findVerifiedUsersByRole(@Param("role") Role role, @Param("verified") Boolean verified);
    
    // Search functionality
    @Query("SELECT u FROM User u WHERE " +
           "(:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:role IS NULL OR u.role = :role)")
    List<User> searchUsers(@Param("username") String username, 
                           @Param("email") String email, 
                           @Param("role") Role role);
    
    // Pagination queries
    Page<User> findByRole(Role role, Pageable pageable);
    
    // Statistics queries
    long countByRole(Role role);
    long countByRoleAndIsVerified(Role role, Boolean isVerified);
}
