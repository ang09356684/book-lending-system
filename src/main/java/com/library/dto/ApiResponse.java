package com.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Unified API Response DTO
 * 
 * @author Library System
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    private boolean success;
    private T data;
    private String message;
    private String error;
    
    /**
     * Create success response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, null);
    }
    
    /**
     * Create success response with data and message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setMessage(message);
        return response;
    }
    
    /**
     * Create error response
     */
    public static <T> ApiResponse<T> error(String error) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setError(error);
        return response;
    }
    
    /**
     * Create error response with message
     */
    public static <T> ApiResponse<T> error(String error, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setError(error);
        response.setMessage(message);
        return response;
    }
}
