package com.github.thibstars.jhaapi.client.history.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Attributes(
        String friendlyName,
        String unitOfMeasurement
) {

}
