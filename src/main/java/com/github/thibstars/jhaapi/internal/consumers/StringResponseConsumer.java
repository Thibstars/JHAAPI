package com.github.thibstars.jhaapi.internal.consumers;

import java.util.Optional;
import java.util.function.Function;
import okhttp3.Response;

/**
 * @author Thibault Helsmoortel
 */
public class StringResponseConsumer extends ResponseConsumer implements Function<Response, Optional<String>> {

    @Override
    public Optional<String> apply(Response response) {
        return onSuccess(response, () -> getResponseBodyString(response));
    }
}
