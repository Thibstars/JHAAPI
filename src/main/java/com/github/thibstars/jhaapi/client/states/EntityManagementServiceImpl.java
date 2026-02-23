package com.github.thibstars.jhaapi.client.states;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.events.EntityEventService;
import com.github.thibstars.jhaapi.client.states.response.State;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of EntityManagementService.
 *
 * @author Thibault Helsmoortel
 */
public class EntityManagementServiceImpl implements EntityManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityManagementServiceImpl.class);

    private final Configuration configuration;
    private final StatesService statesService;

    public EntityManagementServiceImpl(Configuration configuration, StatesService statesService) {
        this.configuration = configuration;
        this.statesService = statesService;
    }

    @Override
    public void initialize() {
        initialize(null);
    }

    @Override
    public void initialize(EntityEventService entityEventService) {
        if (!configuration.isEntityEnabled()) {
            LOGGER.debug("Entity management is not enabled, skipping initialization.");
            return;
        }

        String entityId = configuration.getEntityId();
        LOGGER.info("Initializing managed entity: {}", entityId);

        statesService.getState(entityId).ifPresentOrElse(
                state -> LOGGER.info("Managed entity {} already exists.", entityId),
                () -> {
                    LOGGER.info("Managed entity {} does not exist, creating it.", entityId);
                    statesService.updateState(entityId, "off", configuration.getEntityFriendlyName(), configuration.isEntityReadOnly());
                }
        );

        if (configuration.isEntityShutdownEnabled() && entityEventService != null) {
            LOGGER.info("Registering listener for managed entity {} to shut down application when turned off.", entityId);
            entityEventService.onTurnedOff(entityId, id -> {
                LOGGER.info("Managed entity {} turned off in Home Assistant. Shutting down application...", entityId);
                System.exit(0);
            });
        }

        LOGGER.info("Turning on managed entity: {}", entityId);
        turnOn();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down JHAAPI - Turning off managed entity: {}", entityId);
            turnOff();
        }));
    }

    @Override
    public Optional<State> turnOn() {
        if (!configuration.isEntityEnabled()) {
            LOGGER.warn("Attempted to turn on managed entity, but entity management is not enabled.");
            return Optional.empty();
        }

        return statesService.updateState(configuration.getEntityId(), "on", configuration.getEntityFriendlyName(), configuration.isEntityReadOnly());
    }

    @Override
    public Optional<State> turnOff() {
        if (!configuration.isEntityEnabled()) {
            LOGGER.warn("Attempted to turn off managed entity, but entity management is not enabled.");
            return Optional.empty();
        }

        return statesService.updateState(configuration.getEntityId(), "off", configuration.getEntityFriendlyName(), configuration.isEntityReadOnly());
    }
}
