package com.library.repository;

import com.library.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Role Repository - Data access layer for Role entity
 * 
 * @author Library System
 * @version 1.0.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    // Basic query methods
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}
