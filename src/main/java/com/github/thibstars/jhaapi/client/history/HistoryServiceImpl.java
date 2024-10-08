package com.github.thibstars.jhaapi.client.history;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.history.response.StateChange;
import com.github.thibstars.jhaapi.internal.BaseService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
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
    public List<List<StateChange>> getHistory(OffsetDateTime timeStamp, Set<String> entityIds) {
        LOGGER.info("Getting history");

        if (entityIds == null || entityIds.isEmpty()) {
            throw new IllegalArgumentException("At least 1 entity id must be provided.");
        }

        if (timeStamp == null) {
            timeStamp = OffsetDateTime.now().minusDays(1); // Use same default as the actual API
        }

        return getObjectsOfObjects(timeStamp + "?filter_entity_id=" + String.join(",", entityIds));
    }

    @Override
    public List<List<StateChange>> getHistory(Set<String> entityIds) {
        return getHistory(null, entityIds);
    }
}
