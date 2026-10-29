package com.github.thibstars.jhaapi.client.todo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Todo(
        String entityId,
        String name
) {

}
