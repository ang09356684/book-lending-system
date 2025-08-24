package com.library.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

/**
 * External API Service - Handles external system integration
 * 
 * @author Library System
 * @version 1.0.0
 */
@Service
public class ExternalApiService {
    
    private final RestTemplate restTemplate;
    
    @Value("${external.librarian.verification-url:https://todo.com.tw}")
    private String verificationUrl;
    
    @Value("${external.librarian.authorization-header:todo}")
    private String authorizationHeader;
    
    public ExternalApiService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Verify librarian identity with external system
     * 
     * @param librarianId The librarian identification code
     * @return true if verification successful, false otherwise
     */
    public boolean verifyLibrarian(String librarianId) {
        // Mock verification logic: librarianId must start with uppercase 'L'
        if (librarianId == null || librarianId.trim().isEmpty()) {
            System.out.println("External API verification failed: librarianId is null or empty");
            return false;
        }
        
        boolean isValid = librarianId.startsWith("L");
        
        if (isValid) {
            System.out.println("External API verification successful for librarianId: " + librarianId);
        } else {
            System.out.println("External API verification failed for librarianId: " + librarianId + " (must start with 'L')");
        }
        
        return isValid;
    }
    
    /**
     * Verify librarian identity with custom URL and authorization
     * 
     * @param url Custom verification URL
     * @param authorization Custom authorization header
     * @return true if verification successful, false otherwise
     */
    public boolean verifyLibrarian(String url, String authorization) {
        try {
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authorization);
            
            // Create HTTP entity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Make GET request to external API
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            // Check if request was successful (HTTP 200)
            return response.getStatusCode().is2xxSuccessful();
            
        } catch (RestClientException e) {
            // Log the error (in production, use proper logging)
            System.err.println("External API verification failed: " + e.getMessage());
            return false;
        }
    }
}
