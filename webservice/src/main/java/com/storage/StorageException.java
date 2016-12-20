package com.storage;

/**
 * <h1>Exception Class</h1> 
 * This class creates new RuntimeExceptions while providing an
 * additional message if file operations are not successfully handled.
 * 
 * @author Florian Nachtigall
 * @version 1.0
 * @since 2016-12-20
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}