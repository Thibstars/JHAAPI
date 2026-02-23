package com.github.thibstars.jhaapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.thibstars.jhaapi.internal.exceptions.ConfigurationException;
import com.github.thibstars.jhaapi.internal.interceptors.RequestTokenInterceptor;
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
    private final boolean entityEnabled;
    private final String entityId;
    private final String entityFriendlyName;
    private final boolean entityReadOnly;
    private final boolean entityShutdownEnabled;

    private Configuration(Builder builder) {
        if (builder.baseUrl == null) {
            try {
                String defaultBaseUrl = "http://homeassistant.local:8123/api";
                LOGGER.warn("Base URL not provided, will default to '{}'.", defaultBaseUrl);

                this.baseUrl = URI.create(defaultBaseUrl).toURL();
            } catch (MalformedURLException e) {
                throw new ConfigurationException("Unable to use the default base url.", e);
            }
        } else {
            this.baseUrl = builder.baseUrl;
        }

        if (builder.longLivedAccessToken == null) {
            throw new ConfigurationException("Long-Lived Access Token not provided.");
        }

        this.longLivedAccessToken = builder.longLivedAccessToken;
        this.okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new RequestTokenInterceptor(this))
                .build();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.entityEnabled = builder.entityEnabled;
        this.entityId = builder.entityId;
        this.entityFriendlyName = builder.entityFriendlyName;
        this.entityReadOnly = builder.entityReadOnly;
        this.entityShutdownEnabled = builder.entityShutdownEnabled;
    }

    public Configuration(URL baseUrl, String longLivedAccessToken, boolean entityEnabled, String entityId, String entityFriendlyName, boolean entityReadOnly, boolean entityShutdownEnabled) {
        this(builder().baseUrl(baseUrl).token(longLivedAccessToken).entityEnabled(entityEnabled).entityId(entityId).entityFriendlyName(entityFriendlyName).entityReadOnly(entityReadOnly).entityShutdownEnabled(entityShutdownEnabled));
    }

    public Configuration(URL baseUrl, String longLivedAccessToken, boolean entityEnabled, String entityId, String entityFriendlyName) {
        this(baseUrl, longLivedAccessToken, entityEnabled, entityId, entityFriendlyName, true, false);
    }

    public Configuration(URL baseUrl, String longLivedAccessToken) {
        this(baseUrl, longLivedAccessToken, false, "switch.jhaapi_application", "JHAAPI Application");
    }

    public Configuration(URL baseUrl, String longLivedAccessToken, boolean entityEnabled) {
        this(baseUrl, longLivedAccessToken, entityEnabled, "switch.jhaapi_application", "JHAAPI Application");
    }

    public Configuration(String longLivedAccessToken) {
        this(null, longLivedAccessToken);
    }

    public Configuration(String longLivedAccessToken, boolean entityEnabled) {
        this(null, longLivedAccessToken, entityEnabled);
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

    public boolean isEntityEnabled() {
        return entityEnabled;
    }

    public String getEntityId() {
        return entityId;
    }

    public boolean isEntityReadOnly() {
        return entityReadOnly;
    }

    public boolean isEntityShutdownEnabled() {
        return entityShutdownEnabled;
    }

    public String getEntityFriendlyName() {
        return entityFriendlyName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private URL baseUrl;
        private String longLivedAccessToken;
        private boolean entityEnabled = false;
        private String entityId = "switch.jhaapi_application";
        private String entityFriendlyName = "JHAAPI Application";
        private boolean entityReadOnly = true;
        private boolean entityShutdownEnabled = false;

        public Builder baseUrl(URL baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            try {
                this.baseUrl = URI.create(baseUrl).toURL();
            } catch (MalformedURLException e) {
                throw new ConfigurationException("Invalid base URL: " + baseUrl, e);
            }
            return this;
        }

        public Builder token(String longLivedAccessToken) {
            this.longLivedAccessToken = longLivedAccessToken;
            return this;
        }

        public Builder entityEnabled(boolean entityEnabled) {
            this.entityEnabled = entityEnabled;
            return this;
        }

        public Builder entityId(String entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder entityFriendlyName(String entityFriendlyName) {
            this.entityFriendlyName = entityFriendlyName;
            return this;
        }

        public Builder entityReadOnly(boolean entityReadOnly) {
            this.entityReadOnly = entityReadOnly;
            return this;
        }

        public Builder entityShutdownEnabled(boolean entityShutdownEnabled) {
            this.entityShutdownEnabled = entityShutdownEnabled;
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }
    }
}
