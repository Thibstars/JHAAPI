package com.github.thibstars.jhaapi.internal.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.function.BiFunction;
import okhttp3.Response;

/**
 * @author Thibault Helsmoortel
 */
public class JsonResponseObjectConsumer<T> extends ResponseConsumer implements BiFunction<Response, Class<T>, Optional<T>> {

    private final ObjectMapper objectMapper;

    public JsonResponseObjectConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<T> apply(Response response, Class<T> clazz) {
        return onSucces(response, () -> getResponseBodyString(response)
                .map(responseBodyString -> {
                    try {
                        return objectMapper.readValue(responseBodyString, clazz);
                    } catch (JsonProcessingException e) {
                        handleException(e);
                        return null;
                    }
                }));
    }
}
