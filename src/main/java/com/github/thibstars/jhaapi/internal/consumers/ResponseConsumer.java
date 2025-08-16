package com.github.thibstars.jhaapi.internal.consumers;

import com.github.thibstars.jhaapi.internal.exceptions.ClientException;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Callable;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public abstract class ResponseConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseConsumer.class);

    protected Optional<String> getResponseBodyString(Response response) {
        return Optional.of(response)
                .map(Response::body)
                .map(body -> {
                    try {
                        String result = body.string();
                        body.close(); // Explicitly close the body to prevent resource leaks
                        return result;
                    } catch (IOException e) {
                        handleException(e);
                        throw new RuntimeException("This code is unreachable as handleException throws an exception");
                    }
                });
    }

    protected void handleException(Exception exception) {
        throw new ClientException("Unable to fetch object.", exception);
    }

    protected <T> Optional<T> onSuccess(Response response, Callable<Optional<T>> callable) {
        if (response.isSuccessful()) {
            try {
                return callable.call();
            } catch (Exception e) {
                handleException(e);
                // This code is unreachable as handleException throws an exception
                throw new RuntimeException("Unreachable code");
            }
        } else {
            LOGGER.warn("Call failed with status code: {}", response.code());
        }

        return Optional.empty();
    }
}
