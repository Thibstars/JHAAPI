package com.github.thibstars.jhaapi.client.history;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.BaseService;
import java.time.OffsetDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class HistoryServiceImpl extends BaseService<StateChange> implements HistoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryServiceImpl.class);

    public HistoryServiceImpl(Configuration configuration) {
        super(configuration, "history/period/", StateChange.class);
    }

    @Override
    public List<StateChange> getHistory(OffsetDateTime timeStamp, String entityId) {
        LOGGER.info("Getting history");

        if (timeStamp == null) {
            timeStamp = OffsetDateTime.now().minusDays(1); // Use same default as the actual API
        }

        return getObjectsNestedInOneTooManyBrackets(timeStamp + "?filter_entity_id=" + entityId);
    }
}
