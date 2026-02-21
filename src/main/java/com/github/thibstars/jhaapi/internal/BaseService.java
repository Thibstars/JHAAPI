package com.github.thibstars.jhaapi.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.internal.consumers.JsonResponseListConsumer;
import com.github.thibstars.jhaapi.internal.consumers.JsonResponseListOfListsConsumer;
import com.github.thibstars.jhaapi.internal.consumers.JsonResponseObjectConsumer;
import com.github.thibstars.jhaapi.internal.consumers.StringResponseConsumer;
import com.github.thibstars.jhaapi.internal.exceptions.ClientException;
import com.github.thibstars.jhaapi.internal.exceptions.JHAAPIException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public abstract class BaseService<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);

    private static final StringResponseConsumer STRING_RESPONSE_CONSUMER = new StringResponseConsumer();
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final Configuration configuration;

    private final String url;

    private final Class<T> clazz;

    private final JsonResponseObjectConsumer<T> jsonResponseObjectConsumer;
    private final JsonResponseListConsumer<T> jsonResponseListConsumer;
    private final JsonResponseListOfListsConsumer<T> jsonResponseListOfListsConsumer;

    protected BaseService(Configuration configuration, String url, Class<T> clazz) {
        this.configuration = configuration;
        this.url = url;
        this.clazz = clazz;

        ObjectMapper objectMapper = configuration.getObjectMapper();
        this.jsonResponseObjectConsumer = new JsonResponseObjectConsumer<>(objectMapper);
        this.jsonResponseListConsumer = new JsonResponseListConsumer<>(objectMapper);
        this.jsonResponseListOfListsConsumer = new JsonResponseListOfListsConsumer<>(objectMapper);
    }

    protected Configuration getConfiguration() {
        return configuration;
    }

    public Optional<T> getObject() {
        return getObject("");
    }

    @SuppressWarnings("unchecked") // We can actually safely cast to T as we know what it is via the clazz field
    protected Optional<T> getObject(String path) {
        Request request = new Request.Builder()
                .url(configuration.getBaseUrl() + "/" + url + (path.isEmpty() ? "" : "/" + path))
                .build();

        LOGGER.info("Getting object from url: {}", request.url());

        try (Response response = configuration.getOkHttpClient().newCall(request).execute()) {
            if (clazz.equals(String.class)) {
                return (Optional<T>) STRING_RESPONSE_CONSUMER.apply(response);
            }

            return jsonResponseObjectConsumer.apply(response, clazz);
        } catch (IOException e) {
            handleException(e);
            // This code is unreachable as handleException throws an exception
            throw new RuntimeException("Unreachable code");
        }
    }

    protected List<T> getObjects() {
        Request request = new Request.Builder()
                .url(configuration.getBaseUrl() + "/" + this.url)
                .build();

        return getObjects(request);
    }

    public List<T> getObjects(URIBuilder uriBuilder) {
        try {
            Request request = new Request.Builder()
                    .url(uriBuilder.build().toString())
                    .build();

            return getObjects(request);
        } catch (URISyntaxException e) {
            throw new JHAAPIException(e);
        }
    }

    private List<T> getObjects(Request request) {
        LOGGER.info("Getting objects from url: {}", request.url());

        try (Response response = configuration.getOkHttpClient().newCall(request).execute()) {
            return jsonResponseListConsumer.apply(response, clazz);
        } catch (IOException e) {
            handleException(e);
            // This code is unreachable as handleException throws an exception
            throw new RuntimeException("Unreachable code");
        }
    }

    public List<List<T>> getObjectsOfObjects() {
        return getObjectsOfObjects(url);
    }

    protected List<List<T>> getObjectsOfObjects(String url) {
        String fullUrl = this.url + url;

        Request request = new Request.Builder()
                .url(configuration.getBaseUrl() + "/" + fullUrl)
                .build();

        LOGGER.info("Getting objects of objects from url: {}", request.url());

        try (Response response = configuration.getOkHttpClient().newCall(request).execute()) {
            return jsonResponseListOfListsConsumer.apply(response, clazz);
        } catch (IOException e) {
            handleException(e);
            // This code is unreachable as handleException throws an exception
            throw new RuntimeException("Unreachable code");
        }
    }

    protected Optional<String> post(String url, String body) {
        String fullUrl = this.url + url;

        try {
            configuration.getObjectMapper().readTree(body);
        } catch (JsonProcessingException e) {
            throw new ClientException("Provided body did not appear to be a valid JSON format.", e);
        }

        Request request = new Request.Builder()
                .post(RequestBody.create(body, JSON_MEDIA_TYPE))
                .url(configuration.getBaseUrl() + "/" + fullUrl)
                .build();

        LOGGER.info("Performing POST on url: {}", request.url());

        try (Response response = configuration.getOkHttpClient().newCall(request).execute()) {
            LOGGER.info("Got response with code: {}", response.code());

            return STRING_RESPONSE_CONSUMER.apply(response);
        } catch (IOException e) {
            handleException(e);
            // This code is unreachable as handleException throws an exception
            throw new RuntimeException("Unreachable code");
        }
    }

    public Optional<T> post(String path) {
        String fullUrl = this.url + (path.isEmpty() ? "" : "/" + path);

        Request request = new Request.Builder()
                .post(RequestBody.create("", null))
                .url(configuration.getBaseUrl() + (configuration.getBaseUrl().toString().endsWith("/") ? "" : "/") + fullUrl)
                .build();

        LOGGER.info("Performing POST on url: {}", request.url());

        try (Response response = configuration.getOkHttpClient().newCall(request).execute()) {
            LOGGER.info("Got response with code: {}", response.code());

            return jsonResponseObjectConsumer.apply(response, clazz);
        } catch (IOException e) {
            handleException(e);
            // This code is unreachable as handleException throws an exception
            throw new RuntimeException("Unreachable code");
        }
    }

    protected void postMultipart(String url, MultipartBody body) {
        String fullUrl = this.url + url;

        Request request = new Request.Builder()
                .post(body)
                .url(configuration.getBaseUrl() + "/" + fullUrl)
                .build();

        LOGGER.info("Performing multipart POST on url: {}", request.url());

        try (Response response = configuration.getOkHttpClient().newCall(request).execute()) {
            LOGGER.info("Got response with code: {}", response.code());
            if (response.code() >= 400) {
                String responseBody = response.body() != null ? response.body().string() : "No body";
                LOGGER.error("Error response body: {}", responseBody);
                System.err.println("[DEBUG_LOG] postMultipart error: " + responseBody);
            }
        } catch (IOException e) {
            handleException(e);
            // This code is unreachable as handleException throws an exception
            throw new RuntimeException("Unreachable code");
        }
    }

    protected URIBuilder getUriBuilderFromBaseUrl() {
        try {
            return new URIBuilder(configuration.getBaseUrl().toURI())
                    .appendPathSegments(url);
        } catch (URISyntaxException e) {
            throw new JHAAPIException(e);
        }
    }

    private static void handleException(IOException e) {
        throw new ClientException("Unable to fetch object.", e);
    }
}
