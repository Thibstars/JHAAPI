package com.github.thibstars.jhaapi.client.logbook;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.logbook.response.Log;
import com.github.thibstars.jhaapi.internal.BaseService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class LogbookServiceImpl extends BaseService<Log> implements LogbookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogbookServiceImpl.class);

    public LogbookServiceImpl(Configuration configuration) {
        super(configuration, "logbook", Log.class);
    }

    @Override
    public List<Log> getLogs() {
        return getLogs(null, null, null);
    }

    @Override
    public List<Log> getLogs(String entityId) {
        return getLogs(null, entityId, null);
    }

    @Override
    public List<Log> getLogs(String entityId, OffsetDateTime endTimeStamp) {
        return getLogs(null, entityId, endTimeStamp);
    }

    @Override
    public List<Log> getLogs(OffsetDateTime timeStamp) {
        return getLogs(timeStamp, null, null);
    }

    @Override
    public List<Log> getLogs(OffsetDateTime timeStamp, String entityId) {
        return getLogs(timeStamp, entityId, null);
    }

    @Override
    public List<Log> getLogs(OffsetDateTime timeStamp, OffsetDateTime endTimeStamp) {
        return getLogs(timeStamp, null, endTimeStamp);
    }

    @Override
    public List<Log> getLogs(OffsetDateTime timeStamp, String entityId, OffsetDateTime endTimeStamp) {
        LOGGER.info("Getting logbook");

        if (timeStamp == null) {
            timeStamp = OffsetDateTime.now().minusDays(1); // Use same default as the actual API
        }

        URIBuilder uriBuilder = getUriBuilderFromBaseUrl()
                .appendPathSegments(timeStamp.toString());

        Optional.ofNullable(entityId)
                .ifPresent(actual -> uriBuilder.addParameter("entity", actual));

        Optional.ofNullable(endTimeStamp)
                .ifPresent(actual -> uriBuilder.addParameter("end_time", actual.toString()));

        return getObjects(uriBuilder);
    }
}
