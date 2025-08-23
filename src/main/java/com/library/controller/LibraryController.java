package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.response.LibraryResponse;
import com.library.entity.Library;
import com.library.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/libraries")
@Validated
@Tag(name = "Libraries", description = "Library management endpoints")
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
    @Operation(
        summary = "Get all libraries",
        description = "Retrieve all libraries in the system"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Libraries retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<LibraryResponse>>> getAllLibraries() {
        List<Library> libraries = libraryService.findAll();
        List<LibraryResponse> libraryResponses = libraries.stream()
            .map(library -> new LibraryResponse(
                library.getId(),
                library.getName(),
                library.getAddress(),
                library.getPhone()
            ))
            .toList();
        return ResponseEntity.ok(ApiResponse.success(libraryResponses));
    }
    
    /**
     * Get library by ID
     * 
     * @param id Library ID
     * @return Library information
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get library by ID",
        description = "Retrieve a specific library by its ID"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Library found successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Library not found"
        )
    })
    public ResponseEntity<ApiResponse<LibraryResponse>> getLibrary(
        @Parameter(description = "Library ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        Library library = libraryService.findById(id);
        LibraryResponse libraryResponse = new LibraryResponse(
            library.getId(),
            library.getName(),
            library.getAddress(),
            library.getPhone()
        );
        return ResponseEntity.ok(ApiResponse.success(libraryResponse));
    }
    
    /**
     * Get library by name
     * 
     * @param name Library name
     * @return Library information
     */
    @GetMapping("/name/{name}")
    @Operation(
        summary = "Get library by name",
        description = "Retrieve a library by its name"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Library found successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Library not found"
        )
    })
    public ResponseEntity<ApiResponse<LibraryResponse>> getLibraryByName(
        @Parameter(description = "Library name", required = true, example = "Central Library")
        @PathVariable String name
    ) {
        Library library = libraryService.findByName(name)
            .orElseThrow(() -> new RuntimeException("Library not found"));
        LibraryResponse libraryResponse = new LibraryResponse(
            library.getId(),
            library.getName(),
            library.getAddress(),
            library.getPhone()
        );
        return ResponseEntity.ok(ApiResponse.success(libraryResponse));
    }
    
    /**
     * Create a new library
     * 
     * @param library Library information
     * @return Created library information
     */
    @PostMapping
    @Operation(
        summary = "Create new library",
        description = "Create a new library with the provided information"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Library created successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "message": "Library created successfully",
                        "data": {
                            "id": 1,
                            "name": "Central Library",
                            "address": "123 Main St, City",
                            "phone": "+1-555-0123"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid input data"
        )
    })
    public ResponseEntity<ApiResponse<Library>> createLibrary(
        @Parameter(description = "Library information", required = true)
        @RequestBody Library library
    ) {
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
    @Operation(
        summary = "Update library",
        description = "Update an existing library's information"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Library updated successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Library not found"
        )
    })
    public ResponseEntity<ApiResponse<Library>> updateLibrary(
        @Parameter(description = "Library ID", required = true, example = "1")
        @PathVariable Long id,
        @Parameter(description = "Updated library information", required = true)
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
    @Operation(
        summary = "Delete library",
        description = "Delete a library from the system"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Library deleted successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Library not found"
        )
    })
    public ResponseEntity<ApiResponse<String>> deleteLibrary(
        @Parameter(description = "Library ID", required = true, example = "1")
        @PathVariable Long id
    ) {
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
    @Operation(
        summary = "Check library existence",
        description = "Check if a library exists by name"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Existence checked successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "data": true,
                        "message": "Library exists"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Boolean>> libraryExists(
        @Parameter(description = "Library name", required = true, example = "Central Library")
        @PathVariable String name
    ) {
        boolean exists = libraryService.existsByName(name);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}
