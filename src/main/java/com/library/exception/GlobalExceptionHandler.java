package com.library.exception;

import com.library.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global Exception Handler - Unified error handling for all controllers
 * 
 * @author Library System
 * @version 1.0.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());
        
        String errorMessage = String.join(", ", errors);
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("VALIDATION_ERROR", errorMessage));
    }
    
    /**
     * Handle business logic errors
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        
        // Determine error type based on message content
        if (message.contains("Access denied") || message.contains("Only librarians")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("FORBIDDEN", message));
        } else if (message.contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("NOT_FOUND", message));
        } else if (message.contains("already") || message.contains("Limit reached")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("CONFLICT", message));
        } else {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("BUSINESS_ERROR", message));
        }
    }
    
    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
    }
}
