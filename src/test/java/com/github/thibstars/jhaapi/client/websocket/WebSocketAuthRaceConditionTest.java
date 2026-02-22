package com.github.thibstars.jhaapi.client.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.thibstars.jhaapi.Configuration;
import java.io.IOException;
import java.net.URI;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class WebSocketAuthRaceConditionTest {

    private Configuration configuration;
    private WebSocket webSocket;

    @BeforeEach
    void setUp() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        configuration = Mockito.mock(Configuration.class);
        OkHttpClient okHttpClient = Mockito.mock(OkHttpClient.class);
        webSocket = Mockito.mock(WebSocket.class);

        Mockito.when(configuration.getOkHttpClient()).thenReturn(okHttpClient);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://localhost:8123/api").toURL());
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(configuration.getLongLivedAccessToken()).thenReturn("fake-token");

        Mockito.when(okHttpClient.newWebSocket(Mockito.any(), Mockito.any())).thenReturn(webSocket);
    }

    @Test
    void shouldQueueSubscriptionUntilAuthenticated() {
        WebSocketServiceImpl service = new WebSocketServiceImpl(configuration);
        service.connect();

        // Subscribe BEFORE authentication is complete
        WebSocketService.WebSocketEventListener listener = Mockito.mock(WebSocketService.WebSocketEventListener.class);
        service.subscribeToEvents("test_event", listener);

        // Verify that subscribe_events was NOT sent yet (currently it IS sent, which is the bug)
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(webSocket, Mockito.atMost(0)).send(Mockito.contains("subscribe_events"));

        // Now complete the auth handshake
        service.onMessage(webSocket, "{\"type\": \"auth_required\"}");
        Mockito.verify(webSocket).send(Mockito.contains("\"type\":\"auth\""));

        service.onMessage(webSocket, "{\"type\": \"auth_ok\"}");

        // Now verify that the queued subscribe_events was sent
        Mockito.verify(webSocket).send(Mockito.contains("subscribe_events"));
    }
}
