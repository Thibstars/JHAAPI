package com.github.thibstars.jhaapi.client.states;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.thibstars.jhaapi.client.history.Attributes;
import java.time.OffsetDateTime;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record State(
        Attributes attributes,
        String entityId,
        OffsetDateTime lastChanged,
        String state
) {

}