package com.github.thibstars.jhaapi.client.logbook.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Log(
        String contextUserId,
        String domain,
        String entityId,
        String message,
        String name,
        OffsetDateTime when
) {

}
