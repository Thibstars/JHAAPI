package com.github.thibstars.jhaapi.client.states;

import com.github.thibstars.jhaapi.client.events.EntityEventService;
import com.github.thibstars.jhaapi.client.states.response.State;
import java.util.Optional;

/**
 * Service for managing entities in Home Assistant.
 *
 * @author Thibault Helsmoortel
 */
public interface EntityManagementService {

    /**
     * Initializes the managed entity if enabled in configuration.
     * If the entity doesn't exist, it will be created.
     */
    void initialize();

    /**
     * Initializes the managed entity if enabled in configuration.
     * If the entity doesn't exist, it will be created.
     * Also sets up listening for the entity state if shutdown logic is enabled.
     *
     * @param entityEventService the service to listen for entity events
     */
    void initialize(EntityEventService entityEventService);

    /**
     * Turns the managed entity on.
     *
     * @return an optional containing the updated state
     */
    Optional<State> turnOn();

    /**
     * Turns the managed entity off.
     *
     * @return an optional containing the updated state
     */
    Optional<State> turnOff();

}
