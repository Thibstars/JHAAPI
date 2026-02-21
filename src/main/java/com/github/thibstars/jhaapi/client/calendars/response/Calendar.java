package com.github.thibstars.jhaapi.client.calendars.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Calendar(
        String entityId,
        String name
) {

}
