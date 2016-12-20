package com.storage;

/**
 * <h1>Exception Class</h1> 
 * This class creates new RuntimeExceptions with
 * additional information if file is not found on the server.
 * 
 * @author Florian Nachtigall
 * @version 1.0
 * @since 2016-12-20
 */
public class StorageFileNotFoundException extends StorageException {

    public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}