package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.entity.User;
import com.library.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Controller - Handles user management operations
 * 
 * @author Library System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/users")
@Validated
@Tag(name = "Users", description = "User management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Get user by ID
     * 
     * @param id User ID
     * @return User information
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieve a specific user by their ID"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User found successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    public ResponseEntity<ApiResponse<User>> getUser(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        User user = userService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    /**
     * Get user by username
     * 
     * @param username Username
     * @return User information
     */
    @GetMapping("/username/{username}")
    @Operation(
        summary = "Get user by username",
        description = "Retrieve a user by their username"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User found successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    public ResponseEntity<ApiResponse<User>> getUserByUsername(
        @Parameter(description = "Username", required = true, example = "john_doe")
        @PathVariable String username
    ) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    /**
     * Get user by email
     * 
     * @param email Email
     * @return User information
     */
    @GetMapping("/email/{email}")
    @Operation(
        summary = "Get user by email",
        description = "Retrieve a user by their email address"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User found successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    public ResponseEntity<ApiResponse<User>> getUserByEmail(
        @Parameter(description = "Email address", required = true, example = "john@example.com")
        @PathVariable String email
    ) {
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    /**
     * Get all users by role
     * 
     * @param roleName Role name (USER, LIBRARIAN, ADMIN)
     * @return List of users with the specified role
     */
    @GetMapping("/role/{roleName}")
    @Operation(
        summary = "Get users by role",
        description = "Retrieve all users with a specific role"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<User>>> getUsersByRole(
        @Parameter(description = "Role name", required = true, example = "USER", schema = @Schema(allowableValues = {"USER", "LIBRARIAN", "ADMIN"}))
        @PathVariable String roleName
    ) {
        // This would require RoleService to get Role by name
        // For now, we'll return an empty list as placeholder
        return ResponseEntity.ok(ApiResponse.success(List.of()));
    }
    
    /**
     * Get verified users by role
     * 
     * @param roleName Role name (USER, LIBRARIAN, ADMIN)
     * @return List of verified users with the specified role
     */
    @GetMapping("/role/{roleName}/verified")
    @Operation(
        summary = "Get verified users by role",
        description = "Retrieve all verified users with a specific role"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Verified users retrieved successfully"
        )
    })
    public ResponseEntity<ApiResponse<List<User>>> getVerifiedUsersByRole(
        @Parameter(description = "Role name", required = true, example = "LIBRARIAN", schema = @Schema(allowableValues = {"USER", "LIBRARIAN", "ADMIN"}))
        @PathVariable String roleName
    ) {
        // This would require RoleService to get Role by name
        // For now, we'll return an empty list as placeholder
        return ResponseEntity.ok(ApiResponse.success(List.of()));
    }
    
    /**
     * Update user verification status
     * 
     * @param id User ID
     * @param verified Verification status
     * @return Updated user information
     */
    @PutMapping("/{id}/verification")
    @Operation(
        summary = "Update user verification status",
        description = "Update the verification status of a user"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Verification status updated successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    public ResponseEntity<ApiResponse<User>> updateVerificationStatus(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable Long id,
        @Parameter(description = "Verification status", required = true, example = "true")
        @RequestParam boolean verified
    ) {
        User user = userService.updateVerificationStatus(id, verified);
        return ResponseEntity.ok(ApiResponse.success(user, "Verification status updated successfully"));
    }
    
    /**
     * Count users by role
     * 
     * @param roleName Role name (USER, LIBRARIAN, ADMIN)
     * @return Number of users with the specified role
     */
    @GetMapping("/role/{roleName}/count")
    @Operation(
        summary = "Count users by role",
        description = "Get the number of users with a specific role"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "data": 25,
                        "message": "User count retrieved"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> countUsersByRole(
        @Parameter(description = "Role name", required = true, example = "USER", schema = @Schema(allowableValues = {"USER", "LIBRARIAN", "ADMIN"}))
        @PathVariable String roleName
    ) {
        // This would require RoleService to get Role by name
        // For now, we'll return 0 as placeholder
        return ResponseEntity.ok(ApiResponse.success(0L));
    }
}
