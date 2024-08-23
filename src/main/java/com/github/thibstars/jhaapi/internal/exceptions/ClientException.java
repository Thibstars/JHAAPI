package com.github.thibstars.jhaapi.internal.exceptions;

/**
 * @author Thibault Helsmoortel
 */
public class ClientException extends JHAAPIException {

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Exception exception) {
        super(message, exception);
    }
}
