package com.github.thibstars.jhaapi.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.exceptions.ClientException;
import com.github.thibstars.jhaapi.exceptions.JHAAPIException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.hc.core5.net.URIBuilder;
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

    protected Optional<T> getObject() {
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

    protected List<T> getObjects() {
        return getObjects("");
    }

    protected List<T> getObjects(URIBuilder uriBuilder) {
        try {
            Request request = new Request.Builder()
                    .url(uriBuilder.build().toString())
                    .build();

            return getObjects(request);
        } catch (URISyntaxException e) {
            throw new JHAAPIException(e);
        }
    }

    protected List<T> getObjects(String url) {
        String fullUrl = this.url + url;

        LOGGER.info("Getting objects from url: {}", fullUrl);

        Request request = new Request.Builder()
                .url(configuration.getBaseUrl() + "/" + fullUrl)
                .build();

        return getObjects(request);
    }

    @SuppressWarnings("unchecked") // We can actually safely cast to List<T> as we constructed that collection type before
    private List<T> getObjects(Request request) {
        ResponseBody responseBody;
        T object;
        try (Response response = configuration.getOkHttpClient().newCall(request).execute()) {
            if (response.isSuccessful()) {
                responseBody = Objects.requireNonNull(response.body());
                ObjectMapper objectMapper = configuration.getObjectMapper();
                String responseBodyString = responseBody.string();
                object = objectMapper.readValue(
                        responseBodyString,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
                );
            } else {
                LOGGER.warn("Call failed with status code: {}", response.code());

                return Collections.emptyList();
            }
        } catch (IOException e) {
            throw new ClientException("Unable to fetch objects.", e);
        }

        return (List<T>) object;
    }

    public List<List<T>> getObjectsOfObjects() {
        return getObjectsOfObjects(url);
    }

    @SuppressWarnings("unchecked") // We can actually safely cast to List<T> as we constructed that collection type before
    protected List<List<T>> getObjectsOfObjects(String url) {
        String fullUrl = this.url + url;

        LOGGER.info("Getting objects of objects from url: {}", fullUrl);

        Request request = new Request.Builder()
                .url(configuration.getBaseUrl() + "/" + fullUrl)
                .build();

        ResponseBody responseBody;
        T object;
        try (Response response = configuration.getOkHttpClient().newCall(request).execute()) {
            if (response.isSuccessful()) {
                responseBody = Objects.requireNonNull(response.body());
                ObjectMapper objectMapper = configuration.getObjectMapper();
                String responseBodyString = responseBody.string();
                object = objectMapper.readValue(
                        responseBodyString,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz))
                );
            } else {
                LOGGER.warn("Call failed with status code: {}", response.code());

                return Collections.emptyList();
            }
        } catch (IOException e) {
            throw new ClientException("Unable to fetch objects.", e);
        }

        return (List<List<T>>) object;
    }

    protected URIBuilder getUriBuilderFromBaseUrl() {
        try {
            return new URIBuilder(configuration.getBaseUrl().toURI())
                    .appendPathSegments(url);
        } catch (URISyntaxException e) {
            throw new JHAAPIException(e);
        }
    }
}
