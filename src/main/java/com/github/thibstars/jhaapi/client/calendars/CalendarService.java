package com.github.thibstars.jhaapi.client.calendars;

import com.github.thibstars.jhaapi.client.calendars.response.Calendar;
import com.github.thibstars.jhaapi.client.calendars.response.CalendarEvent;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author Thibault Helsmoortel
 */
public interface CalendarService {

    /**
     * Returns a list of calendar entities.
     *
     * @return a list of calendar entities
     */
    List<Calendar> getCalendars();

    /**
     * Returns a list of calendar events for the given calendar entity and time range.
     *
     * @param entityId the calendar entity id
     * @param start    the start of the time range (optional)
     * @param end      the end of the time range (optional)
     * @return a list of calendar events
     */
    List<CalendarEvent> getCalendarEvents(String entityId, OffsetDateTime start, OffsetDateTime end);

}
