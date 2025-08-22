package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.entity.Library;
import com.library.service.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Library Controller - Handles library management operations
 * 
 * @author Library System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/libraries")
@Validated
public class LibraryController {
    
    private final LibraryService libraryService;
    
    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }
    
    /**
     * Get all libraries
     * 
     * @return List of all libraries
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Library>>> getAllLibraries() {
        List<Library> libraries = libraryService.findAll();
        return ResponseEntity.ok(ApiResponse.success(libraries));
    }
    
    /**
     * Get library by ID
     * 
     * @param id Library ID
     * @return Library information
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Library>> getLibrary(@PathVariable Long id) {
        Library library = libraryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(library));
    }
    
    /**
     * Get library by name
     * 
     * @param name Library name
     * @return Library information
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<Library>> getLibraryByName(@PathVariable String name) {
        Library library = libraryService.findByName(name)
            .orElseThrow(() -> new RuntimeException("Library not found"));
        return ResponseEntity.ok(ApiResponse.success(library));
    }
    
    /**
     * Create a new library
     * 
     * @param library Library information
     * @return Created library information
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Library>> createLibrary(@RequestBody Library library) {
        Library createdLibrary = libraryService.createLibrary(
            library.getName(),
            library.getAddress(),
            library.getPhone()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(createdLibrary, "Library created successfully"));
    }
    
    /**
     * Update library information
     * 
     * @param id Library ID
     * @param library Updated library information
     * @return Updated library information
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Library>> updateLibrary(
        @PathVariable Long id,
        @RequestBody Library library
    ) {
        Library updatedLibrary = libraryService.updateLibrary(
            id,
            library.getName(),
            library.getAddress(),
            library.getPhone()
        );
        
        return ResponseEntity.ok(ApiResponse.success(updatedLibrary, "Library updated successfully"));
    }
    
    /**
     * Delete library
     * 
     * @param id Library ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteLibrary(@PathVariable Long id) {
        libraryService.deleteLibrary(id);
        return ResponseEntity.ok(ApiResponse.success("Library deleted successfully"));
    }
    
    /**
     * Check if library exists by name
     * 
     * @param name Library name
     * @return Boolean indicating if library exists
     */
    @GetMapping("/exists/{name}")
    public ResponseEntity<ApiResponse<Boolean>> libraryExists(@PathVariable String name) {
        boolean exists = libraryService.existsByName(name);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}
