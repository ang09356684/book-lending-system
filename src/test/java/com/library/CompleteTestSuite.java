package com.library;

import com.library.config.TestConfig;
import com.library.repository.BookCopyRepositoryTest;
import com.library.repository.BookRepositoryTest;
import com.library.repository.BorrowRecordRepositoryTest;
import com.library.repository.UserRepositoryTest;
import com.library.service.BookServiceTest;
import com.library.service.BorrowServiceTest;
import com.library.service.ExternalApiServiceTest;
import com.library.service.ScheduledNotificationServiceTest;
import com.library.service.UserServiceTest;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Complete Test Suite
 * Execute all Repository and Service tests in one go, avoiding repeated Spring container startup
 * 
 * @author Library System
 * @version 1.0.0
 */
@Suite
@SelectClasses({
    // Repository Tests
    UserRepositoryTest.class,
    BookRepositoryTest.class,
    BookCopyRepositoryTest.class,
    BorrowRecordRepositoryTest.class,
    
    // Service Tests
    UserServiceTest.class,
    BookServiceTest.class,
    BorrowServiceTest.class,
    ExternalApiServiceTest.class,
    ScheduledNotificationServiceTest.class
})
@SpringBootTest
@Import(TestConfig.class)
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class CompleteTestSuite {
    // Test suite, no additional methods needed
}
