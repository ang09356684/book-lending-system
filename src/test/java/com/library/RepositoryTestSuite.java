package com.library;

import com.library.repository.BookCopyRepositoryTest;
import com.library.repository.BookRepositoryTest;
import com.library.repository.BorrowRecordRepositoryTest;
import com.library.repository.UserRepositoryTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Repository 測試套件
 * 一次性執行所有 Repository 測試，避免重複啟動 Spring 容器
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
public class RepositoryTestSuite {
    // 測試套件，不需要額外方法
}
