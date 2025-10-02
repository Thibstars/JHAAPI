package com.github.thibstars.jhaapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.github.thibstars.jhaapi.internal.exceptions.ConfigurationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Thibault Helsmoortel
 */
class ConfigurationTest {

    @Test
    void shouldUseDefaultBaseUrlWhenNullProvided() {
        String token = "dummy-token";

        Configuration configuration = new Configuration(null, token);

        Assertions.assertEquals("http://homeassistant.local:8123/api", configuration.getBaseUrl().toString());
        ObjectMapper mapper = configuration.getObjectMapper();
        Assertions.assertNotNull(mapper);
        Assertions.assertEquals(PropertyNamingStrategies.SNAKE_CASE, mapper.getPropertyNamingStrategy());
    }

    @Test
    void shouldThrowWhenTokenIsNull() {
        Assertions.assertThrows(ConfigurationException.class, () -> new Configuration(null, null));
    }
}
