package com.github.thibstars.jhaapi.client.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.states.StatesService;
import com.github.thibstars.jhaapi.client.states.StatesServiceImpl;
import com.github.thibstars.jhaapi.client.states.response.State;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the LightService interface.
 * 
 * @author Thibault Helsmoortel
 */
public class LightServiceImpl implements LightService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LightServiceImpl.class);
    private static final String LIGHT_DOMAIN = "light";
    private static final String TURN_ON_SERVICE = "turn_on";
    private static final String TURN_OFF_SERVICE = "turn_off";
    private static final String STATE_ON = "on";
    
    private final ServiceService serviceService;
    private final StatesService statesService;
    private final Configuration configuration;
    
    /**
     * Creates a new LightServiceImpl.
     * 
     * @param configuration the configuration to use
     * @param serviceService the service service to use
     * @param statesService the states service to use
     */
    public LightServiceImpl(Configuration configuration, ServiceService serviceService, StatesService statesService) {
        this.configuration = configuration;
        this.serviceService = serviceService;
        this.statesService = statesService;
    }
    
    /**
     * Creates a new LightServiceImpl.
     * 
     * @param configuration the configuration to use
     */
    public LightServiceImpl(Configuration configuration) {
        this(
            configuration, 
            new ServiceServiceImpl(configuration),
            new StatesServiceImpl(configuration)
        );
    }

    /**
     * Toggles a given light on or off.
     *
     * @param lightName the name of the light to toggle (must not be blank)
     */
    @Override
    public void toggle(String lightName) {
        if (lightName == null || lightName.trim().isEmpty()) {
            throw new IllegalArgumentException("Light name must not be blank");
        }
        
        String entityId = "light." + lightName;
        LOGGER.info("Toggling light: {}", entityId);
        
        try {
            // Create the service data with the entity ID
            ServiceData lightEntity = new ServiceData(entityId);
            String serviceData = configuration.getObjectMapper().writeValueAsString(lightEntity);
            
            // Get the current state of the light
            List<State> states = statesService.getStates(entityId);
            
            if (states.isEmpty()) {
                LOGGER.warn("No state found for light: {}", entityId);
                // Default to turning on if we can't determine the state
                serviceService.callService(LIGHT_DOMAIN, TURN_ON_SERVICE, serviceData);
                return;
            }
            
            // Check if the light is currently on
            boolean isOn = states.stream().anyMatch(state -> STATE_ON.equals(state.state()));
            
            // Toggle the light based on its current state
            if (isOn) {
                LOGGER.info("Light {} is currently on, turning it off", entityId);
                serviceService.callService(LIGHT_DOMAIN, TURN_OFF_SERVICE, serviceData);
            } else {
                LOGGER.info("Light {} is currently off, turning it on", entityId);
                serviceService.callService(LIGHT_DOMAIN, TURN_ON_SERVICE, serviceData);
            }
            
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize light entity data", e);
            throw new RuntimeException("Failed to toggle light", e);
        }
    }
    
    /**
     * Record to represent the service data for a light entity.
     */
    private record ServiceData(String entityId) { }
}