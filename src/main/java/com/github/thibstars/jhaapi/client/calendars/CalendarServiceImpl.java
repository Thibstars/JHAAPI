package com.github.thibstars.jhaapi.client.calendars;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.calendars.response.Calendar;
import com.github.thibstars.jhaapi.client.calendars.response.CalendarEvent;
import com.github.thibstars.jhaapi.internal.BaseService;
import java.time.OffsetDateTime;
import java.util.List;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class CalendarServiceImpl extends BaseService<Calendar> implements CalendarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarServiceImpl.class);

    public CalendarServiceImpl(Configuration configuration) {
        super(configuration, "calendars", Calendar.class);
    }

    @Override
    public List<Calendar> getCalendars() {
        LOGGER.info("Getting calendars");

        return getObjects();
    }

    @Override
    public List<CalendarEvent> getCalendarEvents(String entityId, OffsetDateTime start, OffsetDateTime end) {
        LOGGER.info("Getting calendar events for {}", entityId);

        URIBuilder uriBuilder = getUriBuilderFromBaseUrl()
                .appendPath(entityId);

        if (start != null) {
            uriBuilder.addParameter("start", start.toString());
        }

        if (end != null) {
            uriBuilder.addParameter("end", end.toString());
        }

        return new BaseService<>(getConfiguration(), "calendars", CalendarEvent.class) {
        }.getObjects(uriBuilder);
    }
}
