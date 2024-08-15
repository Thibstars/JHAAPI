package com.github.thibstars.jhaapi.client.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record StateChange(
       Attributes attributes,
       String entityId,
       OffsetDateTime lastChanged,
       OffsetDateTime lastReported,
       OffsetDateTime lastUpdated,
       String state,
       Context context
) {

}