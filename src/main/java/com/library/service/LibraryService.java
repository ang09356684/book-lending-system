package com.library.service;

import com.library.entity.Library;
import com.library.repository.LibraryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Library Service - Business logic for library management
 * 
 * @author Library System
 * @version 1.0.0
 */
@Service
@Transactional
public class LibraryService {
    
    private final LibraryRepository libraryRepository;
    
    public LibraryService(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }
    
    /**
     * Create a new library
     */
    public Library createLibrary(String name, String address, String phone) {
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Library name is required");
        }
        
        if (address == null || address.trim().isEmpty()) {
            throw new RuntimeException("Library address is required");
        }
        
        // Check if library name already exists
        if (libraryRepository.existsByName(name)) {
            throw new RuntimeException("Library name already exists");
        }
        
        // Create library
        Library library = new Library(name, address, phone);
        return libraryRepository.save(library);
    }
    
    /**
     * Find library by name
     */
    public Optional<Library> findByName(String name) {
        return libraryRepository.findByName(name);
    }
    
    /**
     * Get library by ID
     */
    public Library findById(Long id) {
        return libraryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Library not found"));
    }
    
    /**
     * Get all libraries
     */
    public List<Library> findAll() {
        return libraryRepository.findAll();
    }
    
    /**
     * Update library information
     */
    public Library updateLibrary(Long id, String name, String address, String phone) {
        Library library = findById(id);
        
        // Check if new name conflicts with existing library
        if (!library.getName().equals(name) && libraryRepository.existsByName(name)) {
            throw new RuntimeException("Library name already exists");
        }
        
        library.setName(name);
        library.setAddress(address);
        library.setPhone(phone);
        
        return libraryRepository.save(library);
    }
    
    /**
     * Delete library
     */
    public void deleteLibrary(Long id) {
        Library library = findById(id);
        libraryRepository.delete(library);
    }
    
    /**
     * Check if library exists by name
     */
    public boolean existsByName(String name) {
        return libraryRepository.existsByName(name);
    }
}
