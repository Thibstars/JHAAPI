package com.github.thibstars.jhaapi.client.config;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.config.response.Config;
import java.io.IOException;
import java.net.URI;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class ConfigServiceImplTest {

    @Test
    void shouldGetConfig() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://homeassistant:8123/api/").toURL());

        Call call = Mockito.mock(Call.class);
        Response response = Mockito.mock(Response.class);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        Mockito.when(responseBody.string()).thenReturn("""
                {
                   "components":[
                      "sensor.cpuspeed",
                      "frontend",
                      "config.core",
                      "http",
                      "map",
                      "api",
                      "sun",
                      "config",
                      "discovery",
                      "conversation",
                      "recorder",
                      "group",
                      "sensor",
                      "websocket_api",
                      "automation",
                      "config.automation",
                      "config.customize"
                   ],
                   "config_dir":"/home/ha/.homeassistant",
                   "elevation":510,
                   "latitude":45.8781529,
                   "location_name":"Home",
                   "longitude":8.458853651,
                   "time_zone":"Europe/Zurich",
                   "unit_system":{
                      "length":"km",
                      "mass":"g",
                      "temperature":"\\u00b0C",
                      "volume":"L"
                   },
                   "version":"0.56.2",
                   "whitelist_external_dirs":[
                      "/home/ha/.homeassistant/www",
                      "/home/ha/.homeassistant/"
                   ]
                }
                """);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(configuration.getOkHttpClient().newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);

        Config config = Mockito.mock(Config.class);
        Mockito.when(configuration.getObjectMapper().readValue(responseBody.string(), Config.class)).thenReturn(config);

        Config result = new ConfigServiceImpl(configuration).getConfig().orElseThrow();

        Assertions.assertNotNull(result, "Result must not be null.");
        Assertions.assertEquals(config, result, "Result must match the expected.");
    }
}