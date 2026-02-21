package com.github.thibstars.jhaapi.client.config.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CheckConfigResponse(
        String result,
        String errors
) {

}
