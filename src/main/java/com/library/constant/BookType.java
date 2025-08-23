package com.library.constant;

/**
 * Book Type Constants
 * 
 * @author Library System
 * @version 1.0.0
 */
public final class BookType {
    
    /**
     * 圖書 - Traditional books
     */
    public static final String TRADITIONAL = "圖書";
    
    /**
     * 書籍 - Modern books
     */
    public static final String MODERN = "書籍";
    
    /**
     * Default book type
     */
    public static final String DEFAULT = TRADITIONAL;
    
    /**
     * All valid book types
     */
    public static final String[] VALID_TYPES = {TRADITIONAL, MODERN};
    
    /**
     * Check if the given book type is valid
     * 
     * @param bookType the book type to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String bookType) {
        if (bookType == null) {
            return false;
        }
        for (String validType : VALID_TYPES) {
            if (validType.equals(bookType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get all valid book types as a string array
     * 
     * @return array of valid book types
     */
    public static String[] getValidTypes() {
        return VALID_TYPES.clone();
    }
    
    // Prevent instantiation
    private BookType() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
