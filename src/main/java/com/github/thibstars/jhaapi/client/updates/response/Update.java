package com.github.thibstars.jhaapi.client.updates.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Update(
        UpdateAttributes attributes,
        @JsonProperty("entity_id") String entityId,
        @JsonProperty("last_changed") OffsetDateTime lastChanged,
        @JsonProperty("last_updated") OffsetDateTime lastUpdated,
        String state
) {

    public boolean isUpdateAvailable() {
        return "on".equalsIgnoreCase(state);
    }
}
