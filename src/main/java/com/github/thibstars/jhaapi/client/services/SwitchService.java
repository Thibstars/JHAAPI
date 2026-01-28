package com.github.thibstars.jhaapi.client.services;

/**
 * Service for controlling switches.
 *
 * @author Thibault Helsmoortel
 */
public interface SwitchService extends ToggleableService {

    /**
     * Toggles a switch on or off.
     *
     * @param switchName the name of the switch to toggle (must not be blank)
     * @throws IllegalArgumentException if switchName is blank
     */
    @Override
    void toggle(String switchName);
}
