package com.github.thibstars.jhaapi.client.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thibstars.jhaapi.Configuration;
import java.io.IOException;
import java.net.URI;
import java.util.List;
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
class ServiceServiceImplTest {

    @Test
    void shouldGetServices() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://homeassistant:8123/api/").toURL());

        Call call = Mockito.mock(Call.class);
        Response response = Mockito.mock(Response.class);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        Mockito.when(responseBody.string()).thenReturn("""
                [
                    {
                      "domain": "browser",
                      "services": {
                        "browse_url"
                      }
                    },
                    {
                      "domain": "keyboard",
                      "services": {
                        "volume_up",
                        "volume_down"
                      }
                    }
                ]
                """);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(configuration.getOkHttpClient().newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);

        List<Service> events = List.of(
                new Service("browser", "{\"browse_url\"}"),
                new Service("keyboard", "{\"volume_up\",\"volume_down\"}")
        );
        ObjectMapper objectMapper = configuration.getObjectMapper();
        Mockito.when(objectMapper.readValue(responseBody.string(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Service.class))).thenReturn(events);

        List<Service> result = new ServiceServiceImpl(configuration).getServices();

        Assertions.assertNotNull(result, "Result must not be null.");
        Assertions.assertEquals(events, result, "Result must match the expected.");
    }
}