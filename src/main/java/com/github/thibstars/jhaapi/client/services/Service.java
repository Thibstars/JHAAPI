package com.github.thibstars.jhaapi.client.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.thibstars.jhaapi.client.deserializers.RawJsonDeserializer;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Service(
        String domain,

        // Fetching the raw Json of the Services, as it is impossible to be aware of all (default or plugin) service structures
        @JsonDeserialize(using = RawJsonDeserializer.class)
        String services
) {

}
