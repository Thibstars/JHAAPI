package com.github.thibstars.jhaapi.client.config.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UnitSystem(
        String length,
        String mass,
        String temperature,
        String volume,
        String accumulatedPrecipitation,
        String pressure,
        String windSpeed
) {

}
