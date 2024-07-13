package com.github.thibstars.jhaapi.client;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.exceptions.ClientException;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public abstract class BaseService<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);

    private final Configuration configuration;

    private final String url;

    private final Class<T> clazz;

    protected BaseService(Configuration configuration, String url, Class<T> clazz) {
        this.configuration = configuration;
        this.url = url;
        this.clazz = clazz;
    }

    public Optional<T> getObject() {
        LOGGER.info("Getting object from url: {}", url);

        Request request = new Request.Builder()
                .url(configuration.getBaseUrl() + url)
                .build();

        ResponseBody responseBody;
        T object;
        try (Response response = configuration.getOkHttpClient().newCall(request).execute()) {
            if (response.isSuccessful()) {
                responseBody = Objects.requireNonNull(response.body());
                object = configuration.getObjectMapper().readValue(responseBody.string(), clazz);
            } else {
                LOGGER.warn("Call failed with status code: {}", response.code());

                return Optional.empty();
            }
        } catch (IOException e) {
            throw new ClientException("Unable to fetch object.", e);
        }

        return Optional.ofNullable(object);
    }
}
