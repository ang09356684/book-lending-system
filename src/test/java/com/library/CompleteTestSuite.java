package com.library;

import com.library.repository.BookCopyRepositoryTest;
import com.library.repository.BookRepositoryTest;
import com.library.repository.BorrowRecordRepositoryTest;
import com.library.repository.UserRepositoryTest;
import com.library.service.BookServiceTest;
import com.library.service.BorrowServiceTest;
import com.library.service.ExternalApiServiceTest;
import com.library.service.ScheduledNotificationServiceTest;
import com.library.service.UserServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

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
public class CompleteTestSuite {
    // Test suite, no additional methods needed
}
