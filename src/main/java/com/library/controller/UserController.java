package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.response.UserResponse;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.RoleRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final RoleRepository roleRepository;
    
    public UserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
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
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        // Check permissions: users can only view their own data, librarians can view all users
        User currentUser = getCurrentUser();
        if (!currentUser.getRole().getName().equals("LIBRARIAN") && !currentUser.getId().equals(id)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }
        
        User user = userService.findById(id);
        UserResponse userResponse = new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().getName()
        );
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }
    
    /**
     * Get current authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findByEmailWithRole(email)
            .orElseThrow(() -> new RuntimeException("Current user not found"));
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
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(
        @Parameter(description = "Email address", required = true, example = "john@example.com")
        @PathVariable String email
    ) {
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        UserResponse userResponse = new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().getName()
        );
        return ResponseEntity.ok(ApiResponse.success(userResponse));
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
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(
        @Parameter(description = "Role name", required = true, example = "MEMBER", schema = @Schema(allowableValues = {"MEMBER", "LIBRARIAN"}))
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
    public ResponseEntity<ApiResponse<List<UserResponse>>> getVerifiedUsersByRole(
        @Parameter(description = "Role name", required = true, example = "LIBRARIAN", schema = @Schema(allowableValues = {"MEMBER", "LIBRARIAN"}))
        @PathVariable String roleName
    ) {
        // Get role by name
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        
        // Get verified users by role
        List<User> verifiedUsers = userService.findVerifiedUsersByRole(role);
        
        // Convert to UserResponse
        List<UserResponse> userResponses = verifiedUsers.stream()
            .map(user -> new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getName()
            ))
            .toList();
        
        return ResponseEntity.ok(ApiResponse.success(userResponses, 
            "Found " + userResponses.size() + " verified " + roleName + " users"));
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
