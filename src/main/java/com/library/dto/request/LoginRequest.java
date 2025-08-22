package com.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Login Request DTO
 *
 * @author Library System
 * @version 1.0.0
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
