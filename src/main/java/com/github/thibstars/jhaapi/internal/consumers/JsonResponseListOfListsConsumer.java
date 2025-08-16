package com.github.thibstars.jhaapi.internal.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import okhttp3.Response;

/**
 * @author Thibault Helsmoortel
 */
public class JsonResponseListOfListsConsumer<T> extends ResponseConsumer implements BiFunction<Response, Class<T>, List<List<T>>> {

    private final ObjectMapper objectMapper;

    public JsonResponseListOfListsConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked") // We can actually safely cast to List<List<T>>
    @Override
    public List<List<T>> apply(Response response, Class<T> clazz) {
        return onSuccess(response, () -> getResponseBodyString(response)
                .map(responseBodyString -> {
                    try {
                        return ((List<List<T>>) objectMapper.readValue(responseBodyString,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz))));
                    } catch (JsonProcessingException e) {
                        handleException(e);
                        // This code is unreachable as handleException throws an exception
                        throw new RuntimeException("Unreachable code");
                    }
                }))
                .orElse(Collections.emptyList());
    }
}
