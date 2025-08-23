package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.request.LibrarianRegisterRequest;
import com.library.dto.request.RegisterRequest;
import com.library.dto.request.LoginRequest;
import com.library.dto.response.LoginResponse;
import com.library.dto.response.UserResponse;
import com.library.entity.User;
import com.library.security.JwtTokenProvider;
import com.library.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Authentication Controller - Handles user registration and authentication
 * 
 * @author Library System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/auth")
@Validated
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {
    
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    
    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }
    
    /**
     * Register a new user
     * 
     * @param request User registration request
     * @return Registered user information
     */
    @PostMapping("/register")
    @Operation(
        summary = "Register new user",
        description = "Register a new user account with basic information"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.library.dto.ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "message": "User registered successfully",
                        "data": {
                            "id": 1,
                            "username": "john_doe",
                            "email": "john@example.com",
                            "fullName": "John Doe",
                            "role": "USER"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": false,
                        "message": "Validation failed",
                        "errors": ["Username is required", "Email format is invalid"]
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<UserResponse>> register(
        @Parameter(description = "User registration information", required = true)
        @RequestBody @Valid RegisterRequest request
    ) {
        User user = userService.registerUser(
            request.getName(),
            request.getEmail(),
            request.getPassword()
        );
        
        UserResponse userResponse = new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().getName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(userResponse, "User registered successfully"));
    }
    
    /**
     * Register a new librarian with external verification
     * 
     * @param request Librarian registration request
     * @return Registered librarian information
     */
    @PostMapping("/register/librarian")
    @Operation(
        summary = "Register new librarian",
        description = "Register a new librarian account with external verification"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Librarian registered successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid input data or external verification failed"
        )
    })
    public ResponseEntity<ApiResponse<UserResponse>> registerLibrarian(
        @Parameter(description = "Librarian registration information", required = true)
        @RequestBody @Valid LibrarianRegisterRequest request
    ) {
        User librarian = userService.registerLibrarian(
            request.getName(),
            request.getEmail(),
            request.getPassword(),
            request.getLibrarianId()
        );
        
        UserResponse userResponse = new UserResponse(
            librarian.getId(),
            librarian.getName(),
            librarian.getEmail(),
            librarian.getRole().getName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(userResponse, "Librarian registered successfully"));
    }
    

    
    /**
     * Check if email exists
     * 
     * @param email Email to check
     * @return Boolean indicating if email exists
     */
    @GetMapping("/check-email")
    @Operation(
        summary = "Check email availability",
        description = "Check if an email is already registered"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Email availability checked"
        )
    })
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(
        @Parameter(description = "Email to check", required = true, example = "john@example.com")
        @RequestParam String email
    ) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
    
    /**
     * User login with JWT token generation
     * 
     * @param request Login request
     * @return JWT token and user information
     */
    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = "Authenticate user and return JWT token"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.library.dto.ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "message": "Login successful",
                        "data": {
                            "token": "eyJhbGciOiJIUzUxMiJ9...",
                            "tokenType": "Bearer",
                            "user": {
                                "id": 1,
                                "username": "john_doe",
                                "email": "john@example.com",
                                "fullName": "John Doe"
                            }
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": false,
                        "message": "Invalid username or password"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(
        @Parameter(description = "Login credentials", required = true)
        @RequestBody @Valid LoginRequest request
    ) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        User user = userService.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        LoginResponse loginResponse = new LoginResponse(jwt, user.getRole().getName());
        
        return ResponseEntity.ok(ApiResponse.success(loginResponse, "Login successful"));
    }
    
    /**
     * Test JWT token validation
     */
    @GetMapping("/test")
    @Operation(
        summary = "Test JWT token",
        description = "Test if JWT token is valid"
    )
    public ResponseEntity<ApiResponse<String>> testToken() {
        return ResponseEntity.ok(ApiResponse.success("JWT token is valid", "Token validation successful"));
    }
}
