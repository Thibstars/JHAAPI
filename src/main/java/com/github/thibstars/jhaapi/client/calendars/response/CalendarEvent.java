package com.github.thibstars.jhaapi.client.calendars.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CalendarEvent(
        @JsonDeserialize(using = CalendarEvent.EventTimeDeserializer.class)
        OffsetDateTime start,
        @JsonDeserialize(using = CalendarEvent.EventTimeDeserializer.class)
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
	
    /**
     * Home Assistant returns start/end as {@code {"dateTime": "..."}} for timed events and {@code {"date": "..."}}
     * for all-day events (mapped to the start of that day in the system time zone).
     */
    static class EventTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

        @Override
        public OffsetDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            JsonNode node = parser.getCodec().readTree(parser);
            if (node == null || node.isNull()) {
                return null;
            }
            if (node.isTextual()) {
                return parseText(node.asText());
            }
            if (node.hasNonNull("dateTime")) {
                return OffsetDateTime.parse(node.get("dateTime").asText());
            }
            if (node.hasNonNull("date")) {
                return parseText(node.get("date").asText());
            }
            return null;
        }

        private static OffsetDateTime parseText(String text) {
            if (text.length() <= 10) {
                return LocalDate.parse(text).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
            }
            return OffsetDateTime.parse(text);
        }
    }

}
