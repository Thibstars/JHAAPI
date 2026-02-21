package com.github.thibstars.jhaapi.client.calendars.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CalendarEvent(
        OffsetDateTime start,
        OffsetDateTime end,
        String summary,
        String description,
        String location,
        String uid,
        @JsonProperty("recurrence_id")
        String recurrenceId,
        @JsonProperty("rrule")
        String rrule
) {

}
