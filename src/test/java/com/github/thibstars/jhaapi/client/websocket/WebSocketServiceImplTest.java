package com.github.thibstars.jhaapi.client.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.thibstars.jhaapi.Configuration;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class WebSocketServiceImplTest {

    private Configuration configuration;
    private OkHttpClient okHttpClient;
    private WebSocket webSocket;

    @BeforeEach
    void setUp() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        configuration = Mockito.mock(Configuration.class);
        okHttpClient = Mockito.mock(OkHttpClient.class);
        webSocket = Mockito.mock(WebSocket.class);

        Mockito.when(configuration.getOkHttpClient()).thenReturn(okHttpClient);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://localhost:8123/api").toURL());
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(configuration.getLongLivedAccessToken()).thenReturn("fake-token");

        Mockito.when(okHttpClient.newWebSocket(Mockito.any(), Mockito.any())).thenReturn(webSocket);
    }

    @Test
    void shouldConnect() {
        WebSocketServiceImpl service = new WebSocketServiceImpl(configuration);
        service.connect();

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        Mockito.verify(okHttpClient).newWebSocket(requestCaptor.capture(), Mockito.eq(service));
        
        Request request = requestCaptor.getValue();
        Assertions.assertTrue(request.url().toString().endsWith("/api/websocket"));
    }

    @Test
    void shouldHandleAuthHandshake() {
        WebSocketServiceImpl service = new WebSocketServiceImpl(configuration);
        service.connect();

        // Simulate auth_required
        service.onMessage(webSocket, "{\"type\": \"auth_required\"}");

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(webSocket).send(messageCaptor.capture());
        
        Assertions.assertTrue(messageCaptor.getValue().contains("\"type\":\"auth\""));
        Assertions.assertTrue(messageCaptor.getValue().contains("\"access_token\":\"fake-token\""));

        // Simulate auth_ok
        service.onMessage(webSocket, "{\"type\": \"auth_ok\"}");
    }

    @Test
    void shouldSubscribeToEvents() {
        WebSocketServiceImpl service = new WebSocketServiceImpl(configuration);
        service.connect();
        service.onMessage(webSocket, "{\"type\": \"auth_ok\"}");

        WebSocketService.WebSocketEventListener listener = Mockito.mock(WebSocketService.WebSocketEventListener.class);
        int subscriptionId = service.subscribeToEvents("test_event", listener);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(webSocket, Mockito.atLeastOnce()).send(messageCaptor.capture());

        boolean foundSub = false;
        for (String message : messageCaptor.getAllValues()) {
            if (message.contains("\"type\":\"subscribe_events\"")) {
                Assertions.assertTrue(message.contains("\"event_type\":\"test_event\""));
                Assertions.assertTrue(message.contains("\"id\":" + subscriptionId));
                foundSub = true;
            }
        }
        Assertions.assertTrue(foundSub);
    }

    @Test
    void shouldHandleIncomingEvent() {
        WebSocketServiceImpl service = new WebSocketServiceImpl(configuration);
        service.connect();
        service.onMessage(webSocket, "{\"type\": \"auth_ok\"}");

        AtomicReference<JsonNode> receivedPayload = new AtomicReference<>();
        WebSocketService.WebSocketEventListener listener = (eventType, eventPayload) -> receivedPayload.set(eventPayload);
        
        int subscriptionId = service.subscribeToEvents("state_changed", listener);

        // Simulate an event message
        String eventJson = "{" +
                "\"id\": " + subscriptionId + "," +
                "\"type\": \"event\"," +
                "\"event\": {" +
                "  \"event_type\": \"state_changed\"," +
                "  \"data\": {\"entity_id\": \"light.living_room\"}" +
                " }" +
                "}";
        
        service.onMessage(webSocket, eventJson);

        Assertions.assertNotNull(receivedPayload.get());
        Assertions.assertEquals("state_changed", receivedPayload.get().path("event_type").asText());
        Assertions.assertEquals("light.living_room", receivedPayload.get().path("data").path("entity_id").asText());
    }

    @Test
    void shouldUnsubscribe() {
        WebSocketServiceImpl service = new WebSocketServiceImpl(configuration);
        service.connect();
        service.onMessage(webSocket, "{\"type\": \"auth_ok\"}");

        WebSocketService.WebSocketEventListener listener = Mockito.mock(WebSocketService.WebSocketEventListener.class);
        int subscriptionId = service.subscribeToEvents("test", listener);
        
        service.unsubscribe(subscriptionId);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(webSocket, Mockito.atLeastOnce()).send(messageCaptor.capture());

        boolean foundUnsub = false;
        for (String message : messageCaptor.getAllValues()) {
            if (message.contains("\"type\":\"unsubscribe_events\"")) {
                Assertions.assertTrue(message.contains("\"subscription\":" + subscriptionId));
                foundUnsub = true;
            }
        }
        Assertions.assertTrue(foundUnsub);
    }

    @Test
    void shouldClose() {
        WebSocketServiceImpl service = new WebSocketServiceImpl(configuration);
        service.connect();
        service.close();

        Mockito.verify(webSocket).close(Mockito.eq(1000), Mockito.anyString());
    }
}
