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
                        return body.string();
                    } catch (IOException e) {
                        handleException(e);
                        return null;
                    }
                });
    }

    protected void handleException(Exception exception) {
        throw new ClientException("Unable to fetch object.", exception);
    }

    protected <T> Optional<T> onSucces(Response response, Callable<Optional<T>> callable) {
        if (response.isSuccessful()) {
            try {
                return callable.call();
            } catch (Exception e) {
                handleException(e);
                return Optional.empty();
            }
        } else {
            LOGGER.warn("Call failed with status code: {}", response.code());
        }

        return Optional.empty();
    }
}
