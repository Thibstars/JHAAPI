package com.github.thibstars.jhaapi.internal.exceptions;

/**
 * @author Thibault Helsmoortel
 */
public class JHAAPIException extends RuntimeException {

    public JHAAPIException(Exception exception) {
        super(exception);
    }

    public JHAAPIException(String message) {
        super(message);
    }

    public JHAAPIException(String message, Exception exception) {
        super(message, exception);
    }
}
