package com.github.thibstars.jhaapi.client.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.thibstars.jhaapi.client.websocket.WebSocketService;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link EntityEventService} interface.
 *
 * @author Thibault Helsmoortel
 */
public class EntityEventServiceImpl implements EntityEventService, WebSocketService.WebSocketEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityEventServiceImpl.class);
    private static final String EVENT_TYPE_STATE_CHANGED = "state_changed";
    private static final String STATE_ON = "on";
    private static final String STATE_OFF = "off";

    private final WebSocketService webSocketService;
    private final Map<String, Set<Consumer<String>>> onCallbacksByEntityId = new ConcurrentHashMap<>();
    private final Map<String, Set<Consumer<String>>> offCallbacksByEntityId = new ConcurrentHashMap<>();

    private int subscriptionId = -1;

    /**
     * Creates a new EntityEventServiceImpl.
     *
     * @param webSocketService the web socket service to use
     */
    public EntityEventServiceImpl(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @Override
    public void onTurnedOn(String entityId, Consumer<String> callback) {
        onCallbacksByEntityId.computeIfAbsent(entityId, key -> Collections.newSetFromMap(new ConcurrentHashMap<>()))
                .add(callback);
    }

    @Override
    public void onTurnedOff(String entityId, Consumer<String> callback) {
        offCallbacksByEntityId.computeIfAbsent(entityId, key -> Collections.newSetFromMap(new ConcurrentHashMap<>()))
                .add(callback);
    }

    @Override
    public void start() {
        if (subscriptionId == -1) {
            webSocketService.connect();
            subscriptionId = webSocketService.subscribeToEvents(EVENT_TYPE_STATE_CHANGED, this);
            LOGGER.info("Subscribed to state_changed events with ID {}", subscriptionId);
        }
    }

    @Override
    public void stop() {
        if (subscriptionId != -1) {
            webSocketService.unsubscribe(subscriptionId);
            subscriptionId = -1;
            webSocketService.close();
            LOGGER.info("Unsubscribed from state_changed events");
        }
    }

    @Override
    public void onEvent(String eventType, JsonNode eventPayload) {
        if (!EVENT_TYPE_STATE_CHANGED.equals(eventType)) {
            return;
        }

        JsonNode dataNode = eventPayload.path("data");
        String entityId = dataNode.path("entity_id").asText();
        String newState = dataNode.path("new_state").path("state").asText();
        String oldState = dataNode.path("old_state").path("state").asText();

        if (newState.equals(oldState)) {
            return; // State hasn't changed (e.g.: only attributes changed)
        }

        if (STATE_ON.equals(newState)) {
            LOGGER.debug("Entity {} turned on", entityId);
            notifyCallbacks(onCallbacksByEntityId, entityId);
        } else if (STATE_OFF.equals(newState)) {
            LOGGER.debug("Entity {} turned off", entityId);
            notifyCallbacks(offCallbacksByEntityId, entityId);
        }
    }

    private void notifyCallbacks(Map<String, Set<Consumer<String>>> callbacksByEntityId, String entityId) {
        Set<Consumer<String>> callbacks = callbacksByEntityId.get(entityId);
        if (callbacks != null) {
            callbacks.forEach(callback -> {
                try {
                    callback.accept(entityId);
                } catch (Exception e) {
                    LOGGER.error("Error executing callback for entity {}", entityId, e);
                }
            });
        }
    }
}
