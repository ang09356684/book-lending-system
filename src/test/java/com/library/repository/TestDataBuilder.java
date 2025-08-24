package com.library.repository;

import com.library.entity.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Test Data Builder - 幫助建立測試資料，避免 Hibernate 關聯表的複雜性
 * 
 * @author Library System
 * @version 1.0.0
 */
public class TestDataBuilder {
    
    private final TestEntityManager entityManager;
    
    public TestDataBuilder(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * 建立測試角色
     */
    public Role createRole(String name) {
        Role role = new Role();
        role.setName(name);
        return entityManager.persistAndFlush(role);
    }
    
    /**
     * 建立測試使用者
     */
    public User createUser(String name, String email, String password, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setIsVerified(false);
        return entityManager.persistAndFlush(user);
    }
    
    /**
     * 建立測試圖書館
     */
    public Library createLibrary(String name, String address, String phone) {
        Library library = new Library();
        library.setName(name);
        library.setAddress(address);
        library.setPhone(phone);
        return entityManager.persistAndFlush(library);
    }
    
    /**
     * 建立測試書籍
     */
    public Book createBook(String title, String author, Integer publishedYear, String category, String bookType) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublishedYear(publishedYear);
        book.setCategory(category);
        book.setBookType(bookType);
        return entityManager.persistAndFlush(book);
    }
    
    /**
     * 建立測試書籍副本
     */
    public BookCopy createBookCopy(Book book, Library library, Integer copyNumber, String status) {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setBook(book);
        bookCopy.setLibrary(library);
        bookCopy.setCopyNumber(copyNumber);
        bookCopy.setStatus(status);
        return entityManager.persistAndFlush(bookCopy);
    }
    
    /**
     * 建立測試借閱記錄
     */
    public BorrowRecord createBorrowRecord(User user, BookCopy bookCopy, String status) {
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setUser(user);
        borrowRecord.setBookCopy(bookCopy);
        borrowRecord.setStatus(status);
        return entityManager.persistAndFlush(borrowRecord);
    }
    
    /**
     * 清理所有測試資料
     */
    public void clearAllData() {
        entityManager.clear();
        entityManager.flush();
    }
}
