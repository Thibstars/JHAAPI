package com.github.thibstars.jhaapi.client.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Event(
        String event,
        Integer listenerCount
) {

}
