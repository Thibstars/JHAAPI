package com.github.thibstars.jhaapi.client.states;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.history.response.Attributes;
import com.github.thibstars.jhaapi.client.states.response.State;
import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
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
class StatesServiceImplTest {

    @Test
    void shouldGetStates() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://homeassistant:8123/api/").toURL());

        Call call = Mockito.mock(Call.class);
        Response response = Mockito.mock(Response.class);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        Mockito.when(responseBody.string()).thenReturn("""
                [
                  {
                    "attributes": {},
                    "entity_id": "sun.sun",
                    "last_changed": "2016-05-30T21:43:32.418320+00:00",
                    "state": "below_horizon"
                  },
                  {
                    "attributes": {},
                    "entity_id": "process.Dropbox",
                    "last_changed": "2016-05-30T21:43:32.418320+00:00",
                    "state": "on"
                  }
                ]
                """);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(configuration.getOkHttpClient().newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);

        List<State> states = List.of(
                new State(new Attributes(null, null), "sun.sun", OffsetDateTime.parse("2016-05-30T21:43:32.418320+00:00"), "below_horizon"),
                new State(new Attributes(null, null), "process.Dropbox", OffsetDateTime.parse("2016-05-30T21:43:32.418320+00:00"), "on")
        );
        ObjectMapper objectMapper = configuration.getObjectMapper();
        Mockito.when(objectMapper.readValue(responseBody.string(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, State.class))).thenReturn(states);

        List<State> result = new StatesServiceImpl(configuration).getStates();

        Assertions.assertNotNull(result, "Result must not be null.");
        Assertions.assertEquals(states, result, "Result must match the expected.");
    }
}