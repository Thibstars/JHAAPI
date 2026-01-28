package com.github.thibstars.jhaapi.client.services;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.states.StatesService;

/**
 * Implementation of the {@link SwitchService} interface.
 *
 * @author Thibault Helsmoortel
 */
public class SwitchServiceImpl extends ToggleableServiceImpl implements SwitchService {

    /**
     * Creates a new SwitchServiceImpl.
     *
     * @param configuration the configuration to use
     * @param serviceService the service service to use
     * @param statesService the states service to use
     */
    public SwitchServiceImpl(Configuration configuration, ServiceService serviceService, StatesService statesService) {
        super("switch", configuration, serviceService, statesService);
    }

    /**
     * Creates a new SwitchServiceImpl.
     *
     * @param configuration the configuration to use
     */
    public SwitchServiceImpl(Configuration configuration) {
        super("switch", configuration);
    }

}
