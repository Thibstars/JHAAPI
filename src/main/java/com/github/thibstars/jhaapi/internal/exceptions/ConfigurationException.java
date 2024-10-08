package com.github.thibstars.jhaapi.internal.exceptions;

/**
 * @author Thibault Helsmoortel
 */
public class ConfigurationException extends JHAAPIException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Exception exception) {
        super(message, exception);
    }
}
