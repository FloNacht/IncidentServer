package com.model;

/**
 * <h1>Test Object for HTTP Calls</h1> 
 * This class can be instantiated to test rest connection and accessibility of the server
 * 
 * @author Florian Nachtigall
 * @version 1.0
 * @since 2016-12-20
 */
public class Greeting {

    private final long id;
    private final String content;
    
    public Greeting(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}