package com.library;

import com.library.service.BookServiceTest;
import com.library.service.BorrowServiceTest;
import com.library.service.ExternalApiServiceTest;
import com.library.service.ScheduledNotificationServiceTest;
import com.library.service.UserServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Service Test Suite
 * Execute all Service tests in one go, avoiding repeated Spring container startup
 * 
 * @author Library System
 * @version 1.0.0
 */
@Suite
@SelectClasses({
    UserServiceTest.class,
    BookServiceTest.class,
    BorrowServiceTest.class,
    ExternalApiServiceTest.class,
    ScheduledNotificationServiceTest.class
})
public class ServiceTestSuite {
    // Test suite, no additional methods needed
}
