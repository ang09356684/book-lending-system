package com.library.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Test Configuration
 * Optimized configuration for all repository and service tests
 * 
 * @author Library System
 * @version 1.0.0
 */
@TestConfiguration
@TestPropertySource(properties = {
    // Database configuration
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    
    // JPA optimization
    "spring.jpa.show-sql=false",
    "spring.jpa.properties.hibernate.format_sql=false",
    "spring.jpa.properties.hibernate.use_sql_comments=false",
    
    // Performance optimization
    "spring.main.lazy-initialization=true",
    "spring.jpa.defer-datasource-initialization=false",
    
    // Disable unnecessary features for testing
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration",
    
    // JWT test configuration
    "jwt.secret=testSecretKeyForTestingPurposesOnlyDoNotUseInProduction",
    "jwt.expiration=86400000",
    
    // Disable CSRF for testing
    "spring.security.csrf.enabled=false"
})
@EnableTransactionManagement
public class TestConfig {
    
    // Add any additional test beans here if needed
    
}
