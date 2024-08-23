package com.github.thibstars.jhaapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.thibstars.jhaapi.internal.interceptors.RequestTokenInterceptor;
import com.github.thibstars.jhaapi.internal.exceptions.ConfigurationException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    private final URL baseUrl;
    private final String longLivedAccessToken;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    public Configuration(URL baseUrl, String longLivedAccessToken) {
        if (baseUrl == null) {
            try {
                String defaultBaseUrl = "http://homeassistant.local:8123/api";
                LOGGER.warn("Base URL not provided, will default to '{}'.", defaultBaseUrl);

                this.baseUrl = URI.create(defaultBaseUrl).toURL();
            } catch (MalformedURLException e) {
                throw new ConfigurationException("Unable to use the default base url.", e);
            }
        } else {
            this.baseUrl = baseUrl;
        }

        if (longLivedAccessToken == null) {
            throw new ConfigurationException("Long-Lived Access Token not provided.");
        }

        this.longLivedAccessToken = longLivedAccessToken;
        this.okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new RequestTokenInterceptor(this))
                .build();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    public Configuration(String longLivedAccessToken) {
        this(null, longLivedAccessToken);
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public String getLongLivedAccessToken() {
        return longLivedAccessToken;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
