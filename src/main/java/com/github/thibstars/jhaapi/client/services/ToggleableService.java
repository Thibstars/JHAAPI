package com.github.thibstars.jhaapi.client.services;

/**
 * Service for controlling toggleable entities (e.g., lights, switches).
 *
 * @author Thibault Helsmoortel
 */
public interface ToggleableService {

    /**
     * Toggles an entity on or off.
     *
     * @param entityName the name of the entity to toggle (must not be blank)
     * @throws IllegalArgumentException if entityName is blank
     */
    void toggle(String entityName);
}
