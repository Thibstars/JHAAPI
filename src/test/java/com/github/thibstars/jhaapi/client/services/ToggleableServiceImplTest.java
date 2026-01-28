package com.github.thibstars.jhaapi.client.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.states.StatesService;
import com.github.thibstars.jhaapi.client.states.response.State;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test class for {@link ToggleableServiceImpl}.
 *
 * @author Thibault Helsmoortel
 */
class ToggleableServiceImplTest {

    @Test
    void shouldToggleFromOnToOff() throws JsonProcessingException {
        Configuration configuration = Mockito.mock(Configuration.class);
        ServiceService serviceService = Mockito.mock(ServiceService.class);
        StatesService statesService = Mockito.mock(StatesService.class);

        String domain = "input_boolean";
        String entityName = "my_boolean";
        String entityId = domain + "." + entityName;

        State onState = Mockito.mock(State.class);
        Mockito.when(onState.state()).thenReturn("on");
        Mockito.when(statesService.getState(entityId)).thenReturn(Optional.of(onState));

        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"" + entityId + "\"}");

        ToggleableServiceImpl toggleableService = new ToggleableServiceImpl(domain, configuration, serviceService, statesService);

        toggleableService.toggle(entityName);

        Mockito.verify(serviceService).callService(domain, "turn_off", "{\"entity_id\":\"" + entityId + "\"}");
        Mockito.verify(serviceService, Mockito.never()).callService(domain, "turn_on", "{\"entity_id\":\"" + entityId + "\"}");
    }

    @Test
    void shouldToggleFromOffToOn() throws JsonProcessingException {
        Configuration configuration = Mockito.mock(Configuration.class);
        ServiceService serviceService = Mockito.mock(ServiceService.class);
        StatesService statesService = Mockito.mock(StatesService.class);

        String domain = "input_boolean";
        String entityName = "my_boolean";
        String entityId = domain + "." + entityName;

        State offState = Mockito.mock(State.class);
        Mockito.when(offState.state()).thenReturn("off");
        Mockito.when(statesService.getState(entityId)).thenReturn(Optional.of(offState));

        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"" + entityId + "\"}");

        ToggleableServiceImpl toggleableService = new ToggleableServiceImpl(domain, configuration, serviceService, statesService);

        toggleableService.toggle(entityName);

        Mockito.verify(serviceService).callService(domain, "turn_on", "{\"entity_id\":\"" + entityId + "\"}");
        Mockito.verify(serviceService, Mockito.never()).callService(domain, "turn_off", "{\"entity_id\":\"" + entityId + "\"}");
    }

    @Test
    void shouldTurnOnWhenNoStateFound() throws JsonProcessingException {
        Configuration configuration = Mockito.mock(Configuration.class);
        ServiceService serviceService = Mockito.mock(ServiceService.class);
        StatesService statesService = Mockito.mock(StatesService.class);

        String domain = "input_boolean";
        String entityName = "my_boolean";
        String entityId = domain + "." + entityName;

        Mockito.when(statesService.getState(entityId)).thenReturn(Optional.empty());

        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"" + entityId + "\"}");

        ToggleableServiceImpl toggleableService = new ToggleableServiceImpl(domain, configuration, serviceService, statesService);

        toggleableService.toggle(entityName);

        Mockito.verify(serviceService).callService(domain, "turn_on", "{\"entity_id\":\"" + entityId + "\"}");
        Mockito.verify(serviceService, Mockito.never()).callService(domain, "turn_off", "{\"entity_id\":\"" + entityId + "\"}");
    }

    @Test
    void shouldThrowExceptionWhenEntityNameIsNull() {
        Configuration configuration = Mockito.mock(Configuration.class);
        ToggleableServiceImpl toggleableService = new ToggleableServiceImpl("input_boolean", configuration);

        IllegalArgumentException exception = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> toggleableService.toggle(null)
        );

        Assertions.assertEquals("Entity name must not be blank", exception.getMessage());
    }

    @Test
    void shouldHandleJsonProcessingException() throws JsonProcessingException {
        Configuration configuration = Mockito.mock(Configuration.class);
        ServiceService serviceService = Mockito.mock(ServiceService.class);
        StatesService statesService = Mockito.mock(StatesService.class);

        String domain = "input_boolean";
        String entityName = "my_boolean";
        String entityId = domain + "." + entityName;

        State offState = Mockito.mock(State.class);
        Mockito.when(offState.state()).thenReturn("off");
        Mockito.when(statesService.getState(entityId)).thenReturn(Optional.of(offState));

        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenThrow(new JsonProcessingException("Test exception") {});

        ToggleableServiceImpl toggleableService = new ToggleableServiceImpl(domain, configuration, serviceService, statesService);

        RuntimeException exception = Assertions.assertThrows(
            RuntimeException.class,
            () -> toggleableService.toggle(entityName)
        );

        Assertions.assertEquals("Failed to toggle " + domain, exception.getMessage());
        Assertions.assertInstanceOf(JsonProcessingException.class, exception.getCause());
    }
}
