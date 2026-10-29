package com.github.thibstars.jhaapi.client.todo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.thibstars.jhaapi.internal.deserializers.EventTimeDeserializer;
import java.time.OffsetDateTime;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TodoItem(
        String summary,
        String uid,
        String status,
        String description,
        @JsonDeserialize(using = EventTimeDeserializer.class)
        OffsetDateTime due
) {

}
