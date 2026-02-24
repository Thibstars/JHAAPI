package com.github.thibstars.jhaapi.client.sun.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SunAttributes(
        @JsonProperty("next_dawn")
        OffsetDateTime nextDawn,
        @JsonProperty("next_dusk")
        OffsetDateTime nextDusk,
        @JsonProperty("next_midnight")
        OffsetDateTime nextMidnight,
        @JsonProperty("next_noon")
        OffsetDateTime nextNoon,
        @JsonProperty("next_rising")
        OffsetDateTime nextRising,
        @JsonProperty("next_setting")
        OffsetDateTime nextSetting,
        Double elevation,
        Double azimuth,
        Boolean rising,
        @JsonProperty("friendly_name")
        String friendlyName
) {
}
