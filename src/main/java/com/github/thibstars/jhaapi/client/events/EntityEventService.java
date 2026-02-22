package com.github.thibstars.jhaapi.client.events;

import java.util.function.Consumer;

/**
 * Service to listen for entity on/off events via WebSocket.
 *
 * @author Thibault Helsmoortel
 */
public interface EntityEventService {

    /**
     * Listens for an entity being turned on.
     *
     * @param entityId the ID of the entity to listen to
     * @param callback the callback to run when the entity is turned on
     */
    void onTurnedOn(String entityId, Consumer<String> callback);

    /**
     * Listens for an entity being turned off.
     *
     * @param entityId the ID of the entity to listen to
     * @param callback the callback to run when the entity is turned off
     */
    void onTurnedOff(String entityId, Consumer<String> callback);

    /**
     * Starts listening for events.
     */
    void start();

    /**
     * Stops listening for events.
     */
    void stop();
}
