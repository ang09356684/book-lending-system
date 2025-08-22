package com.library.repository;

import com.library.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Library Repository - Data access layer for Library entity
 * 
 * @author Library System
 * @version 1.0.0
 */
@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {
    
    // Basic query methods
    Optional<Library> findByName(String name);
    boolean existsByName(String name);
}
