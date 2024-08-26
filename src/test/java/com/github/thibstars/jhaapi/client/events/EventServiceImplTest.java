package com.github.thibstars.jhaapi.client.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.events.response.Event;
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
class EventServiceImplTest {

    @Test
    void shouldGetEvents() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://homeassistant:8123/api/").toURL());

        Call call = Mockito.mock(Call.class);
        Response response = Mockito.mock(Response.class);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        Mockito.when(responseBody.string()).thenReturn("""
                [
                    {
                      "event": "state_changed",
                      "listener_count": 5
                    },
                    {
                      "event": "time_changed",
                      "listener_count": 2
                    }
                ]
                """);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(configuration.getOkHttpClient().newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);

        List<Event> events = List.of(
                new Event("state_changed", 5),
                new Event("time_changed", 2)
        );
        ObjectMapper objectMapper = configuration.getObjectMapper();
        Mockito.when(objectMapper.readValue(responseBody.string(), objectMapper.getTypeFactory().constructCollectionType(List.class, Event.class))).thenReturn(events);

        List<Event> result = new EventServiceImpl(configuration).getEvents();

        Assertions.assertNotNull(result, "Result must not be null.");
        Assertions.assertEquals(events, result, "Result must match the expected.");
    }
}