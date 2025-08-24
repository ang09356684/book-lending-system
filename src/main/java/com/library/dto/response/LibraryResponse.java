package com.library.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Library Response DTO - Contains only necessary library information for API responses
 * 
 * @author Library System
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
public class LibraryResponse {
    
    private Long id;
    private String name;
    private String address;
    private String phone;
    
    public LibraryResponse(Long id, String name, String address, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }
}
