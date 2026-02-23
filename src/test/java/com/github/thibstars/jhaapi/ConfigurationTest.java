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

    @Test
    void shouldHaveDefaultEntitySettings() {
        Configuration configuration = new Configuration("token");
        Assertions.assertFalse(configuration.isEntityEnabled());
        Assertions.assertEquals("switch.jhaapi_application", configuration.getEntityId());
        Assertions.assertEquals("JHAAPI Application", configuration.getEntityFriendlyName());
        Assertions.assertTrue(configuration.isEntityReadOnly());
        Assertions.assertFalse(configuration.isEntityShutdownEnabled());
    }

    @Test
    void shouldApplyCustomEntitySettings() {
        Configuration configuration = new Configuration(null, "token", true, "switch.my_app", "My App");
        Assertions.assertTrue(configuration.isEntityEnabled());
        Assertions.assertEquals("switch.my_app", configuration.getEntityId());
        Assertions.assertEquals("My App", configuration.getEntityFriendlyName());
        Assertions.assertTrue(configuration.isEntityReadOnly());
        Assertions.assertFalse(configuration.isEntityShutdownEnabled());
    }

    @Test
    void shouldSupportBuilder() {
        Configuration configuration = Configuration.builder()
                .baseUrl("http://ha:8123/api")
                .token("my-token")
                .entityEnabled(true)
                .entityId("switch.test")
                .entityFriendlyName("Test App")
                .entityReadOnly(false)
                .entityShutdownEnabled(true)
                .build();

        Assertions.assertEquals("http://ha:8123/api", configuration.getBaseUrl().toString());
        Assertions.assertEquals("my-token", configuration.getLongLivedAccessToken());
        Assertions.assertTrue(configuration.isEntityEnabled());
        Assertions.assertEquals("switch.test", configuration.getEntityId());
        Assertions.assertEquals("Test App", configuration.getEntityFriendlyName());
        Assertions.assertFalse(configuration.isEntityReadOnly());
        Assertions.assertTrue(configuration.isEntityShutdownEnabled());
    }
}
