package com.github.thibstars.jhaapi.client.events;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.internal.BaseService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class EventServiceImpl extends BaseService<Event> implements EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    public EventServiceImpl(Configuration configuration) {
        super(configuration, "events", Event.class);
    }

    @Override
    public List<Event> getEvents() {
        LOGGER.info("Getting events");

        return getObjects();
    }
}
