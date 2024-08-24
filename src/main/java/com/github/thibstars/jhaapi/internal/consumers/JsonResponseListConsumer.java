package com.github.thibstars.jhaapi.internal.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import okhttp3.Response;

/**
 * @author Thibault Helsmoortel
 */
public class JsonResponseListConsumer<T> extends ResponseConsumer implements BiFunction<Response, Class<T>, List<T>> {

    private final ObjectMapper objectMapper;

    public JsonResponseListConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked") // We can actually safely cast to List<T>
    @Override
    public List<T> apply(Response response, Class<T> clazz) {
        return onSucces(response, () -> getResponseBodyString(response)
                .map(responseBodyString -> {
                    try {
                        return ((List<T>) objectMapper.readValue(responseBodyString,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)));
                    } catch (JsonProcessingException e) {
                        handleException(e);
                        return null;
                    }
                }))
                .orElse(new ArrayList<>());
    }
}
