package com.github.thibstars.jhaapi.client.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thibstars.jhaapi.client.websocket.WebSocketService;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class EntityEventServiceImplTest {

    private WebSocketService webSocketService;
    private EntityEventServiceImpl entityEventService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.webSocketService = Mockito.mock(WebSocketService.class);
        this.entityEventService = new EntityEventServiceImpl(webSocketService);
    }

    @Test
    void shouldStartAndSubscribe() {
        entityEventService.start();

        Mockito.verify(webSocketService).connect();
        Mockito.verify(webSocketService).subscribeToEvents(Mockito.eq("state_changed"), Mockito.eq(entityEventService));
    }

    @Test
    void shouldStopAndUnsubscribe() {
        Mockito.when(webSocketService.subscribeToEvents(Mockito.anyString(), Mockito.any())).thenReturn(123);
        entityEventService.start();
        entityEventService.stop();

        Mockito.verify(webSocketService).unsubscribe(123);
        Mockito.verify(webSocketService).close();
    }

    @Test
    void shouldTriggerOnTurnedOn() throws Exception {
        AtomicBoolean called = new AtomicBoolean(false);
        String entityId = "light.living_room";
        entityEventService.onTurnedOn(entityId, id -> {
            Assertions.assertEquals(entityId, id);
            called.set(true);
        });

        JsonNode eventPayload = objectMapper.readTree("{" +
                "  \"event_type\": \"state_changed\"," +
                "  \"data\": {" +
                "    \"entity_id\": \"light.living_room\"," +
                "    \"new_state\": {\"state\": \"on\"}," +
                "    \"old_state\": {\"state\": \"off\"}" +
                "  }" +
                "}");

        entityEventService.onEvent("state_changed", eventPayload);

        Assertions.assertTrue(called.get(), "Callback should have been called");
    }

    @Test
    void shouldTriggerOnTurnedOff() throws Exception {
        AtomicBoolean called = new AtomicBoolean(false);
        String entityId = "switch.coffee_maker";
        entityEventService.onTurnedOff(entityId, id -> {
            Assertions.assertEquals(entityId, id);
            called.set(true);
        });

        JsonNode eventPayload = objectMapper.readTree("{" +
                "  \"event_type\": \"state_changed\"," +
                "  \"data\": {" +
                "    \"entity_id\": \"switch.coffee_maker\"," +
                "    \"new_state\": {\"state\": \"off\"}," +
                "    \"old_state\": {\"state\": \"on\"}" +
                "  }" +
                "}");

        entityEventService.onEvent("state_changed", eventPayload);

        Assertions.assertTrue(called.get(), "Callback should have been called");
    }

    @Test
    void shouldNotTriggerWhenStateDidNotChange() throws Exception {
        AtomicBoolean called = new AtomicBoolean(false);
        String entityId = "light.living_room";
        entityEventService.onTurnedOn(entityId, id -> called.set(true));

        JsonNode eventPayload = objectMapper.readTree("{" +
                "  \"event_type\": \"state_changed\"," +
                "  \"data\": {" +
                "    \"entity_id\": \"light.living_room\"," +
                "    \"new_state\": {\"state\": \"on\"}," +
                "    \"old_state\": {\"state\": \"on\"}" +
                "  }" +
                "}");

        entityEventService.onEvent("state_changed", eventPayload);

        Assertions.assertFalse(called.get(), "Callback should NOT have been called");
    }

    @Test
    void shouldNotTriggerForDifferentEntity() throws Exception {
        AtomicBoolean called = new AtomicBoolean(false);
        entityEventService.onTurnedOn("light.kitchen", id -> called.set(true));

        JsonNode eventPayload = objectMapper.readTree("{" +
                "  \"event_type\": \"state_changed\"," +
                "  \"data\": {" +
                "    \"entity_id\": \"light.living_room\"," +
                "    \"new_state\": {\"state\": \"on\"}," +
                "    \"old_state\": {\"state\": \"off\"}" +
                "  }" +
                "}");

        entityEventService.onEvent("state_changed", eventPayload);

        Assertions.assertFalse(called.get(), "Callback should NOT have been called");
    }
}
