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
 * Test class for {@link SwitchServiceImpl}.
 *
 * @author Thibault Helsmoortel
 */
class SwitchServiceImplTest {

    @Test
    void shouldToggleSwitchFromOnToOff() throws JsonProcessingException {
        // Arrange
        Configuration configuration = Mockito.mock(Configuration.class);
        ServiceService serviceService = Mockito.mock(ServiceService.class);
        StatesService statesService = Mockito.mock(StatesService.class);

        String switchName = "myAwesomeSwitch";
        String entityId = "switch.myAwesomeSwitch";

        // Mock the state response to indicate the switch is ON
        State onState = Mockito.mock(State.class);
        Mockito.when(onState.state()).thenReturn("on");
        Mockito.when(statesService.getState(entityId)).thenReturn(Optional.of(onState));

        // Mock the ObjectMapper to return a valid JSON string
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"" + entityId + "\"}");

        // Create the service under test
        SwitchServiceImpl switchService = new SwitchServiceImpl(configuration, serviceService, statesService);

        // Act
        switchService.toggle(switchName);

        // Assert
        Mockito.verify(serviceService).callService("switch", "turn_off", "{\"entity_id\":\"" + entityId + "\"}");
        Mockito.verify(serviceService, Mockito.never()).callService("switch", "turn_on", "{\"entity_id\":\"" + entityId + "\"}");
    }

    @Test
    void shouldToggleSwitchFromOffToOn() throws JsonProcessingException {
        // Arrange
        Configuration configuration = Mockito.mock(Configuration.class);
        ServiceService serviceService = Mockito.mock(ServiceService.class);
        StatesService statesService = Mockito.mock(StatesService.class);

        String switchName = "myAwesomeSwitch";
        String entityId = "switch.myAwesomeSwitch";

        // Mock the state response to indicate the switch is OFF
        State offState = Mockito.mock(State.class);
        Mockito.when(offState.state()).thenReturn("off");
        Mockito.when(statesService.getState(entityId)).thenReturn(Optional.of(offState));

        // Mock the ObjectMapper to return a valid JSON string
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"" + entityId + "\"}");

        // Create the service under test
        SwitchServiceImpl switchService = new SwitchServiceImpl(configuration, serviceService, statesService);

        // Act
        switchService.toggle(switchName);

        // Assert
        Mockito.verify(serviceService).callService("switch", "turn_on", "{\"entity_id\":\"" + entityId + "\"}");
        Mockito.verify(serviceService, Mockito.never()).callService("switch", "turn_off", "{\"entity_id\":\"" + entityId + "\"}");
    }

    @Test
    void shouldTurnOnSwitchWhenNoStateFound() throws JsonProcessingException {
        // Arrange
        Configuration configuration = Mockito.mock(Configuration.class);
        ServiceService serviceService = Mockito.mock(ServiceService.class);
        StatesService statesService = Mockito.mock(StatesService.class);

        String switchName = "myAwesomeSwitch";
        String entityId = "switch.myAwesomeSwitch";

        // Mock the state response to return an empty Optional (no state found)
        Mockito.when(statesService.getState(entityId)).thenReturn(Optional.empty());

        // Mock the ObjectMapper to return a valid JSON string
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"" + entityId + "\"}");

        // Create the service under test
        SwitchServiceImpl switchService = new SwitchServiceImpl(configuration, serviceService, statesService);

        // Act
        switchService.toggle(switchName);

        // Assert
        Mockito.verify(serviceService).callService("switch", "turn_on", "{\"entity_id\":\"" + entityId + "\"}");
        Mockito.verify(serviceService, Mockito.never()).callService("switch", "turn_off", "{\"entity_id\":\"" + entityId + "\"}");
    }

    @Test
    void shouldThrowExceptionWhenSwitchNameIsNull() {
        // Arrange
        Configuration configuration = Mockito.mock(Configuration.class);
        ServiceService serviceService = Mockito.mock(ServiceService.class);
        StatesService statesService = Mockito.mock(StatesService.class);

        SwitchServiceImpl switchService = new SwitchServiceImpl(configuration, serviceService, statesService);

        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> switchService.toggle(null)
        );

        Assertions.assertEquals("Entity name must not be blank", exception.getMessage());
    }

}
