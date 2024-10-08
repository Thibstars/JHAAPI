package com.github.thibstars.jhaapi.client.states;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.states.response.State;
import com.github.thibstars.jhaapi.internal.BaseService;
import java.util.List;
import java.util.Optional;
import org.apache.hc.core5.net.URIBuilder;
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
        return getStates(null);
    }

    @Override
    public List<State> getStates(String entityId) {
        if (entityId != null) {
            LOGGER.info("Getting states of entity {}", entityId);
        } else {
            LOGGER.info("Getting states");
        }

        URIBuilder uriBuilderFromBaseUrl = getUriBuilderFromBaseUrl();

        Optional.ofNullable(entityId)
                .ifPresent(uriBuilderFromBaseUrl::appendPath);

        return getObjects(uriBuilderFromBaseUrl);
    }
}
