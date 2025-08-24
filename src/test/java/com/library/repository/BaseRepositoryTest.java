package com.library.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base Repository Test Class
 * Provides unified configuration for all repository tests
 * 
 * @author Library System
 * @version 1.0.0
 */
@DataJpaTest
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
    "spring.jpa.defer-datasource-initialization=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
public abstract class BaseRepositoryTest {
    // Base class for all repository tests
}
