package com.library;

import com.library.repository.BookCopyRepositoryTest;
import com.library.repository.BookRepositoryTest;
import com.library.repository.BorrowRecordRepositoryTest;
import com.library.repository.UserRepositoryTest;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

/**
 * Repository Test Suite
 * Execute all Repository tests in one go, avoiding repeated Spring container startup
 * 
 * @author Library System
 * @version 1.0.0
 */
@Suite
@SelectClasses({
    UserRepositoryTest.class,
    BookRepositoryTest.class,
    BookCopyRepositoryTest.class,
    BorrowRecordRepositoryTest.class
})
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.jpa.show-sql=false",
    "spring.jpa.properties.hibernate.format_sql=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RepositoryTestSuite {
    // Test suite, no additional methods needed
}
