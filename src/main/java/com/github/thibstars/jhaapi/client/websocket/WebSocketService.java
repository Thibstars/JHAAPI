package com.github.thibstars.jhaapi.client.websocket;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Simple WebSocket service to interact with Home Assistant's WebSocket API and receive live events.
 *
 * @author Thibault Helsmoortel
 */
public interface WebSocketService {

    /**
     * Connects to the Home Assistant WebSocket API and performs authentication.
     */
    void connect();

    /**
     * Closes the WebSocket connection.
     */
    void close();

    /**
     * Subscribes to a Home Assistant event type (e.g. "state_changed").
     * Incoming events for this subscription will be delivered to the provided listener.
     *
     * @param eventType the Home Assistant event type
     * @param listener  the listener that will receive events for this subscription
     * @return the subscription id assigned by this client (maps to the id used in WS messages)
     */
    int subscribeToEvents(String eventType, WebSocketEventListener listener);

    /**
     * Unsubscribes a previously created subscription.
     */
    void unsubscribe(int subscriptionId);

    /**
     * Listener for WebSocket lifecycle and events.
     */
    interface WebSocketEventListener {

        default void onOpen() {
        }

        default void onClosed(int code, String reason) {
        }

        default void onFailure(Throwable throwable) {
        }

        void onEvent(String eventType, JsonNode eventPayload);
    }
}
