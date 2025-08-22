package com.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Librarian registration request DTO
 * 
 * @author Library System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LibrarianRegisterRequest extends RegisterRequest {
    
    @NotBlank(message = "Librarian ID is required")
    private String librarianId;
}
