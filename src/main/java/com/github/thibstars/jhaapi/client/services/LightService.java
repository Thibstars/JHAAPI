package com.github.thibstars.jhaapi.client.services;

/**
 * Service for controlling lights.
 * 
 * @author Thibault Helsmoortel
 */
public interface LightService {

    /**
     * Toggles a light on or off.
     * 
     * @param lightName the name of the light to toggle (must not be blank)
     * @throws IllegalArgumentException if lightName is blank
     */
    void toggle(String lightName);
}