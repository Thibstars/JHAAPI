package com.github.thibstars.jhaapi.client.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.states.StatesService;
import com.github.thibstars.jhaapi.client.states.StatesServiceImpl;
import com.github.thibstars.jhaapi.client.states.response.State;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic implementation of the {@link ToggleableService} interface.
 *
 * @author Thibault Helsmoortel
 */
public class ToggleableServiceImpl implements ToggleableService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToggleableServiceImpl.class);
    private static final String TURN_ON_SERVICE = "turn_on";
    private static final String TURN_OFF_SERVICE = "turn_off";
    private static final String STATE_ON = "on";

    private final String domain;
    private final ServiceService serviceService;
    private final StatesService statesService;
    private final Configuration configuration;

    /**
     * Creates a new ToggleableServiceImpl.
     *
     * @param domain the domain of the entities (e.g., "light", "switch")
     * @param configuration the configuration to use
     * @param serviceService the service service to use
     * @param statesService the states service to use
     */
    public ToggleableServiceImpl(String domain, Configuration configuration, ServiceService serviceService, StatesService statesService) {
        this.domain = domain;
        this.configuration = configuration;
        this.serviceService = serviceService;
        this.statesService = statesService;
    }

    /**
     * Creates a new ToggleableServiceImpl.
     *
     * @param domain the domain of the entities (e.g., "light", "switch")
     * @param configuration the configuration to use
     */
    public ToggleableServiceImpl(String domain, Configuration configuration) {
        this(
            domain,
            configuration,
            new ServiceServiceImpl(configuration),
            new StatesServiceImpl(configuration)
        );
    }

    /**
     * Toggles a given entity on or off.
     *
     * @param entityName the name of the entity to toggle (must not be blank)
     */
    @Override
    public void toggle(String entityName) {
        if (entityName == null || entityName.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity name must not be blank");
        }

        String entityId = domain + "." + entityName;
        LOGGER.info("Toggling {}: {}", domain, entityId);

        try {
            // Create the service data with the entity ID
            ServiceData entityData = new ServiceData(entityId);
            String serviceData = configuration.getObjectMapper().writeValueAsString(entityData);

            // Get the current state of the entity
            Optional<State> stateOptional = statesService.getState(entityId);

            if (stateOptional.isEmpty()) {
                LOGGER.warn("No state found for {}: {}", domain, entityId);
                // Default to turning on if we can't determine the state
                serviceService.callService(domain, TURN_ON_SERVICE, serviceData);
                return;
            }

            // Check if the entity is currently on
            boolean isOn = STATE_ON.equals(stateOptional.get().state());

            // Toggle the entity based on its current state
            if (isOn) {
                LOGGER.info("{} {} is currently on, turning it off", domain, entityId);
                serviceService.callService(domain, TURN_OFF_SERVICE, serviceData);
            } else {
                LOGGER.info("{} {} is currently off, turning it on", domain, entityId);
                serviceService.callService(domain, TURN_ON_SERVICE, serviceData);
            }

        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize entity data for domain {}", domain, e);
            throw new RuntimeException("Failed to toggle " + domain, e);
        }
    }

    /**
     * Record to represent the service data for an entity.
     */
    private record ServiceData(String entityId) { }
}
