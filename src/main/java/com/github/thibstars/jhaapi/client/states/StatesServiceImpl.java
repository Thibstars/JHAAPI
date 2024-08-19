package com.github.thibstars.jhaapi.client.states;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.BaseService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class StatesServiceImpl extends BaseService<State> implements StatesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatesServiceImpl.class);

    public StatesServiceImpl(Configuration configuration) {
        super(configuration, "states", State.class);
    }

    @Override
    public List<State> getStates() {
        LOGGER.info("Getting states");

        return getObjects();
    }
}
