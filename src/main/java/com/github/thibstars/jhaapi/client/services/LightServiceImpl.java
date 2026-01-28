package com.github.thibstars.jhaapi.client.services;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.states.StatesService;

/**
 * Implementation of the {@link LightService} interface.
 * 
 * @author Thibault Helsmoortel
 */
public class LightServiceImpl extends ToggleableServiceImpl implements LightService {

    /**
     * Creates a new LightServiceImpl.
     * 
     * @param configuration the configuration to use
     * @param serviceService the service service to use
     * @param statesService the states service to use
     */
    public LightServiceImpl(Configuration configuration, ServiceService serviceService, StatesService statesService) {
        super("light", configuration, serviceService, statesService);
    }
    
    /**
     * Creates a new LightServiceImpl.
     * 
     * @param configuration the configuration to use
     */
    public LightServiceImpl(Configuration configuration) {
        super("light", configuration);
    }

}