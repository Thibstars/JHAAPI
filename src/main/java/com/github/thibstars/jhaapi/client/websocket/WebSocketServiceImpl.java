package com.github.thibstars.jhaapi.client.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.thibstars.jhaapi.Configuration;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimal WebSocket client for Home Assistant real-time events.
 *
 * @author Thibault Helsmoortel
 */
public class WebSocketServiceImpl extends WebSocketListener implements WebSocketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServiceImpl.class);

    private final Configuration configuration;
    private final OkHttpClient okHttpClient;

    private final AtomicInteger idSequence = new AtomicInteger(1);
    private final Map<Integer, ListenerRegistration> listenersById = new ConcurrentHashMap<>();
    private final Queue<String> messageBuffer = new LinkedList<>();

    private volatile WebSocket webSocket;
    private volatile boolean authenticated = false;

    public WebSocketServiceImpl(Configuration configuration) {
        this.configuration = configuration;
        this.okHttpClient = configuration.getOkHttpClient();
    }

    @Override
    public synchronized void connect() {
        if (webSocket != null) {
            return; // already connected/connecting
        }

        String baseUrl = configuration.getBaseUrl().toString();
        String websocketUrl = baseUrl.startsWith("https") ? baseUrl.replaceFirst("https", "wss") : baseUrl.replaceFirst("http", "ws");
        if (!websocketUrl.endsWith("/")) {
            websocketUrl += "/";
        }
        if (!websocketUrl.endsWith("websocket")) {
            websocketUrl += "websocket";
        }

        Request request = new Request.Builder()
                .url(websocketUrl.replaceFirst("ws", "http")) // OkHttp 5/OkHttp internally may normalize ws to http for Request
                .build();

        LOGGER.info("Connecting to Home Assistant WebSocket at {}", websocketUrl);
        this.webSocket = okHttpClient.newWebSocket(request, this);
    }

    @Override
    public synchronized void close() {
        if (webSocket != null) {
            webSocket.close(1000, "client closing");
            webSocket = null;
            authenticated = false;
            listenersById.clear();
        }
    }

    @Override
    public int subscribeToEvents(String eventType, WebSocketEventListener listener) {
        if (webSocket == null) {
            connect();
        }
        int id = idSequence.getAndIncrement();
        listenersById.put(id, new ListenerRegistration(eventType, listener));

        ObjectNode message = configuration.getObjectMapper().createObjectNode();
        message.put("id", id);
        message.put("type", "subscribe_events");
        message.put("event_type", eventType);
        String payload = message.toString();
        LOGGER.info("Subscribing to events '{}' with id {}", eventType, id);
        sendMessage(payload);

        return id;
    }

    @Override
    public void unsubscribe(int subscriptionId) {
        if (webSocket == null) {
            connect();
        }
        ListenerRegistration listenerRegistration = listenersById.remove(subscriptionId);
        if (listenerRegistration == null || webSocket == null) {
            return;
        }
        ObjectNode message = configuration.getObjectMapper().createObjectNode();
        message.put("id", idSequence.getAndIncrement());
        message.put("type", "unsubscribe_events");
        message.put("subscription", subscriptionId);
        sendMessage(message.toString());
    }

    private synchronized void sendMessage(String payload) {
        if (authenticated) {
            webSocket.send(payload);
        } else {
            LOGGER.info("WebSocket not authenticated yet, buffering message: {}", payload);
            messageBuffer.add(payload);
        }
    }

    // WebSocket callbacks
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        LOGGER.info("WebSocket opened: {}", response);
        // Expect an auth_required message next. Nothing to do here.
        notifyAllOpen();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            JsonNode node = configuration.getObjectMapper().readTree(text);
            String type = node.path("type").asText();
            switch (type) {
                case "auth_required" -> doAuth();
                case "auth_ok" -> {
                    authenticated = true;
                    LOGGER.info("WebSocket authentication successful");
                    flushBuffer();
                }
                case "auth_invalid" -> {
                    authenticated = false;
                    String message = node.path("message").asText();
                    LOGGER.error("WebSocket authentication failed: {}", message);
                }
                case "event" -> handleEvent(node);
                case "result" -> handleResult(node);
                default -> LOGGER.debug("WebSocket message: {}", text);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to handle WebSocket message: {}", text, e);
            notifyAllFailure(e);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        // Home Assistant uses text frames; just ignore/bubble up
        onMessage(webSocket, bytes.utf8());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        LOGGER.info("WebSocket closing: {} {}", code, reason);
        webSocket.close(code, reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        LOGGER.info("WebSocket closed: {} {}", code, reason);
        this.webSocket = null;
        this.authenticated = false;
        notifyAllClosed(code, reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
        LOGGER.error("WebSocket failure", throwable);
        notifyAllFailure(throwable);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    private synchronized void flushBuffer() {
        if (webSocket == null || !authenticated) {
            return;
        }
        while (!messageBuffer.isEmpty()) {
            String payload = messageBuffer.poll();
            LOGGER.info("Flushing buffered message: {}", payload);
            webSocket.send(payload);
        }
    }

    private void doAuth() {
        if (webSocket == null) {
            return;
        }
        ObjectNode authentication = configuration.getObjectMapper().createObjectNode();
        authentication.put("type", "auth");
        authentication.put("access_token", configuration.getLongLivedAccessToken());
        webSocket.send(authentication.toString());
    }

    private void handleResult(JsonNode node) {
        boolean success = node.path("success").asBoolean(true);
        if (!success) {
            LOGGER.warn("WebSocket result not successful: {}", node);
        }
    }

    private void handleEvent(JsonNode node) {
        int subscriptionId = node.path("id").asInt(-1);
        JsonNode eventNode = node.path("event");
        String eventType = eventNode.path("event_type").asText();
        ListenerRegistration listenerRegistration = listenersById.get(subscriptionId);
        if (listenerRegistration != null) {
            try {
                listenerRegistration.listener().onEvent(eventType, eventNode);
            } catch (Exception e) {
                LOGGER.error("Listener threw for subscription {}", subscriptionId, e);
            }
        } else {
            // No specific registration (unknown id) — broadcast to all matching eventType listeners (rare)
            listenersById.forEach((id, registration) -> {
                if (registration.eventType().equals(eventType)) {
                    try {
                        registration.listener().onEvent(eventType, eventNode);
                    } catch (Exception ignored) {
                    }
                }
            });
        }
    }

    private void notifyAllOpen() {
        listenersById.values().forEach(listenerRegistration -> safe(() -> listenerRegistration.listener().onOpen()));
    }

    private void notifyAllClosed(int code, String reason) {
        listenersById.values()
                .forEach(listenerRegistration -> safe(() -> listenerRegistration.listener().onClosed(code, reason)));
    }

    private void notifyAllFailure(Throwable throwable) {
        listenersById.values()
                .forEach(listenerRegistration -> safe(() -> listenerRegistration.listener().onFailure(throwable)));
    }

    private static void safe(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception ignored) {
        }
    }

    private record ListenerRegistration(String eventType, WebSocketEventListener listener) {

    }
}
