package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Response DTO - Contains only necessary user information for API responses
 * 
 * @author Library System
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
public class UserResponse {
    
    private Long id;
    private String name;
    private String email;
    private String roleName;
    
    public UserResponse(Long id, String name, String email, String roleName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roleName = roleName;
    }
}
