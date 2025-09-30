package com.github.thibstars.jhaapi.client.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.states.StatesService;
import com.github.thibstars.jhaapi.client.states.response.State;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test class for {@link LightServiceImpl}.
 * 
 * @author @author Thibault Helsmoortel
 */
class LightServiceImplTest {

    @Test
    void shouldToggleLightFromOnToOff() throws JsonProcessingException {
        // Arrange
        Configuration configuration = Mockito.mock(Configuration.class);
        ServiceService serviceService = Mockito.mock(ServiceService.class);
        StatesService statesService = Mockito.mock(StatesService.class);
        
        String lightName = "myAwesomeLight";
        String entityId = "light.myAwesomeLight";
        
        // Mock the state response to indicate the light is ON
        State onState = Mockito.mock(State.class);
        Mockito.when(onState.state()).thenReturn("on");
        Mockito.when(statesService.getStates(entityId)).thenReturn(List.of(onState));
        
        // Mock the ObjectMapper to return a valid JSON string
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"" + entityId + "\"}");
        
        // Create the service under test
        LightServiceImpl lightService = new LightServiceImpl(configuration, serviceService, statesService);
        
        // Act
        lightService.toggle(lightName);
        
        // Assert
        Mockito.verify(serviceService).callService("light", "turn_off", "{\"entity_id\":\"" + entityId + "\"}");
        Mockito.verify(serviceService, Mockito.never()).callService("light", "turn_on", "{\"entity_id\":\"" + entityId + "\"}");
    }
    
    @Test
    void shouldToggleLightFromOffToOn() throws JsonProcessingException {
        // Arrange
        Configuration configuration = Mockito.mock(Configuration.class);
        ServiceService serviceService = Mockito.mock(ServiceService.class);
        StatesService statesService = Mockito.mock(StatesService.class);
        
        String lightName = "myAwesomeLight";
        String entityId = "light.myAwesomeLight";
        
        // Mock the state response to indicate the light is OFF
        State offState = Mockito.mock(State.class);
        Mockito.when(offState.state()).thenReturn("off");
        Mockito.when(statesService.getStates(entityId)).thenReturn(List.of(offState));
        
        // Mock the ObjectMapper to return a valid JSON string
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"" + entityId + "\"}");
        
        // Create the service under test
        LightServiceImpl lightService = new LightServiceImpl(configuration, serviceService, statesService);
        
        // Act
        lightService.toggle(lightName);
        
        // Assert
        Mockito.verify(serviceService).callService("light", "turn_on", "{\"entity_id\":\"" + entityId + "\"}");
        Mockito.verify(serviceService, Mockito.never()).callService("light", "turn_off", "{\"entity_id\":\"" + entityId + "\"}");
    }
    
    @Test
    void shouldTurnOnLightWhenNoStateFound() throws JsonProcessingException {
        // Arrange
        Configuration configuration = Mockito.mock(Configuration.class);
        ServiceService serviceService = Mockito.mock(ServiceService.class);
        StatesService statesService = Mockito.mock(StatesService.class);
        
        String lightName = "myAwesomeLight";
        String entityId = "light.myAwesomeLight";
        
        // Mock the state response to return an empty list (no state found)
        Mockito.when(statesService.getStates(entityId)).thenReturn(Collections.emptyList());
        
        // Mock the ObjectMapper to return a valid JSON string
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"" + entityId + "\"}");
        
        // Create the service under test
        LightServiceImpl lightService = new LightServiceImpl(configuration, serviceService, statesService);
        
        // Act
        lightService.toggle(lightName);
        
        // Assert
        Mockito.verify(serviceService).callService("light", "turn_on", "{\"entity_id\":\"" + entityId + "\"}");
        Mockito.verify(serviceService, Mockito.never()).callService("light", "turn_off", "{\"entity_id\":\"" + entityId + "\"}");
    }
    
    @Test
    void shouldThrowExceptionWhenLightNameIsNull() {
        // Arrange
        Configuration configuration = Mockito.mock(Configuration.class);
        ServiceService serviceService = Mockito.mock(ServiceService.class);
        StatesService statesService = Mockito.mock(StatesService.class);
        
        LightServiceImpl lightService = new LightServiceImpl(configuration, serviceService, statesService);
        
        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> lightService.toggle(null)
        );
        
        Assertions.assertEquals("Light name must not be blank", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenLightNameIsEmpty() {
        // Arrange
        Configuration configuration = Mockito.mock(Configuration.class);
        ServiceService serviceService = Mockito.mock(ServiceService.class);
        StatesService statesService = Mockito.mock(StatesService.class);
        
        LightServiceImpl lightService = new LightServiceImpl(configuration, serviceService, statesService);
        
        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> lightService.toggle("")
        );
        
        Assertions.assertEquals("Light name must not be blank", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenLightNameIsBlank() {
        // Arrange
        Configuration configuration = Mockito.mock(Configuration.class);
        ServiceService serviceService = Mockito.mock(ServiceService.class);
        StatesService statesService = Mockito.mock(StatesService.class);
        
        LightServiceImpl lightService = new LightServiceImpl(configuration, serviceService, statesService);
        
        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> lightService.toggle("   ")
        );
        
        Assertions.assertEquals("Light name must not be blank", exception.getMessage());
    }
    
    @Test
    void shouldHandleJsonProcessingException() throws JsonProcessingException {
        // Arrange
        Configuration configuration = Mockito.mock(Configuration.class);
        ServiceService serviceService = Mockito.mock(ServiceService.class);
        StatesService statesService = Mockito.mock(StatesService.class);
        
        String lightName = "myAwesomeLight";
        String entityId = "light.myAwesomeLight";
        
        // Mock the state response
        State offState = Mockito.mock(State.class);
        Mockito.when(offState.state()).thenReturn("off");
        Mockito.when(statesService.getStates(entityId)).thenReturn(List.of(offState));
        
        // Mock the ObjectMapper to throw JsonProcessingException
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenThrow(new JsonProcessingException("Test exception") {});
        
        // Create the service under test
        LightServiceImpl lightService = new LightServiceImpl(configuration, serviceService, statesService);
        
        // Act & Assert
        RuntimeException exception = Assertions.assertThrows(
            RuntimeException.class,
            () -> lightService.toggle(lightName)
        );
        
        Assertions.assertEquals("Failed to toggle light", exception.getMessage());
        Assertions.assertInstanceOf(JsonProcessingException.class, exception.getCause());
    }
}