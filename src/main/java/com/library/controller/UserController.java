package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.request.UpdateUserRequest;
import com.library.dto.response.UserResponse;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.RoleRepository;
import com.library.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

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
        
        // Always use findByEmailWithRole to ensure role is loaded
        User user = userService.findByEmailWithRole(email)
            .orElseThrow(() -> new RuntimeException("Current user not found: " + email));
        
        // Ensure role is loaded
        if (user.getRole() == null) {
            throw new RuntimeException("User role not found for: " + email);
        }
        
        return user;
    }
    
    /**
     * Check if current user is a librarian
     */
    private boolean isLibrarian() {
        User currentUser = getCurrentUser();
        return "LIBRARIAN".equals(currentUser.getRole().getName());
    }
    
    /**
     * Check librarian permissions and throw exception if not authorized
     */
    private void checkLibrarianPermission() {
        if (!isLibrarian()) {
            throw new RuntimeException("Access denied. Only librarians can perform this operation.");
        }
    }
    
    /**
     * Debug endpoint to check current user
     */
    @GetMapping("/debug/current")
    @Operation(
        summary = "Debug current user",
        description = "Get current authenticated user information for debugging"
    )
    public ResponseEntity<ApiResponse<Object>> debugCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = userService.findByEmailWithRole(email)
                .orElse(null);
                
            if (user == null) {
                return ResponseEntity.ok(ApiResponse.success(Map.of(
                    "email", email,
                    "error", "User not found"
                )));
            }
            
            return ResponseEntity.ok(ApiResponse.success(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole() != null ? user.getRole().getName() : "NULL",
                "isVerified", user.getIsVerified()
            )));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success(Map.of(
                "error", e.getMessage(),
                "stackTrace", e.getStackTrace()[0].toString()
            )));
        }
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
        description = "Retrieve a user by their email address. **Librarian access only.**"
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
        // Check librarian permissions
        checkLibrarianPermission();
        
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
        description = "Retrieve all users with a specific role. **Librarian access only.**"
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
        // Check librarian permissions
        checkLibrarianPermission();
        
        // Get role by name
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        
        // Get users by role
        List<User> users = userService.findByRole(role);
        
        // Convert to UserResponse
        List<UserResponse> userResponses = users.stream()
            .map(user -> new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getName()
            ))
            .toList();
        
        return ResponseEntity.ok(ApiResponse.success(userResponses));
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
        description = "Update the verification status of a user. **Librarian access only.**"
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
    public ResponseEntity<ApiResponse<UserResponse>> updateVerificationStatus(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable Long id,
        @Parameter(description = "Verification status", required = true, example = "true")
        @RequestParam boolean verified
    ) {
        // Check librarian permissions
        checkLibrarianPermission();
        
        User user = userService.updateVerificationStatus(id, verified);
        
        UserResponse userResponse = new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().getName()
        );
        
        return ResponseEntity.ok(ApiResponse.success(userResponse, "Verification status updated successfully"));
    }
    
    /**
     * Update user information
     * 
     * @param id User ID
     * @param request Update user request
     * @return Updated user information
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update user",
        description = "Update user information"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User updated successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid input data"
        )
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable Long id,
        @Parameter(description = "Update user information", required = true)
        @RequestBody @Valid UpdateUserRequest request
    ) {
        // Check permissions: users can only update their own data, librarians can update all users
        User currentUser = getCurrentUser();
        if (!currentUser.getRole().getName().equals("LIBRARIAN") && !currentUser.getId().equals(id)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied. You can only update your own information."));
        }
        
        User user = userService.updateUser(id, request.getName(), request.getEmail(), request.getPassword());
        
        UserResponse userResponse = new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().getName()
        );
        
        return ResponseEntity.ok(ApiResponse.success(userResponse, "User updated successfully"));
    }
    

}
