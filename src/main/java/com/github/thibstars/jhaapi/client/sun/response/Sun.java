package com.github.thibstars.jhaapi.client.sun.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Sun(
        SunAttributes attributes,
        String entityId,
        OffsetDateTime lastChanged,
        String state
) {

    public boolean isAboveHorizon() {
        return "above_horizon".equalsIgnoreCase(state);
    }

    public boolean isBelowHorizon() {
        return "below_horizon".equalsIgnoreCase(state);
    }
}
