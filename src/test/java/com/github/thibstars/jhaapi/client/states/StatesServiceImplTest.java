package com.github.thibstars.jhaapi.client.states;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.history.response.Attributes;
import com.github.thibstars.jhaapi.client.states.response.State;
import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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

    @Test
    void shouldGetState() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://homeassistant:8123/api/").toURL());

        Call call = Mockito.mock(Call.class);
        Response response = Mockito.mock(Response.class);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        Mockito.when(responseBody.string()).thenReturn("""
                {
                  "attributes": {},
                  "entity_id": "sun.sun",
                  "last_changed": "2016-05-30T21:43:32.418320+00:00",
                  "state": "below_horizon"
                }
                """);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(configuration.getOkHttpClient().newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);

        State state = new State(new Attributes(null, null), "sun.sun", OffsetDateTime.parse("2016-05-30T21:43:32.418320+00:00"), "below_horizon");

        Mockito.when(configuration.getObjectMapper().readValue(responseBody.string(), State.class)).thenReturn(state);

        Optional<State> result = new StatesServiceImpl(configuration).getState("sun.sun");

        Assertions.assertTrue(result.isPresent(), "Result must be present.");
        Assertions.assertEquals(state, result.get(), "Result must match the expected.");
    }

    @Test
    void shouldUpdateState() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://homeassistant:8123/api/").toURL());

        Call call = Mockito.mock(Call.class);
        Response response = Mockito.mock(Response.class);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        Mockito.when(responseBody.string()).thenReturn("""
                {
                  "attributes": {"friendly_name": "JHAAPI Application"},
                  "entity_id": "switch.jhaapi_application",
                  "last_changed": "2026-02-22T15:40:00+00:00",
                  "state": "on"
                }
                """);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(configuration.getOkHttpClient().newCall(ArgumentMatchers.argThat(request ->
                request.url().toString().endsWith("/api/states/switch.jhaapi_application")
        ))).thenReturn(call);

        State state = new State(new Attributes("JHAAPI Application", null), "switch.jhaapi_application", OffsetDateTime.parse("2026-02-22T15:40:00+00:00"), "on");

        Mockito.when(configuration.getObjectMapper().readValue(ArgumentMatchers.anyString(), ArgumentMatchers.eq(State.class))).thenReturn(state);

        Optional<State> result = new StatesServiceImpl(configuration).updateState("switch.jhaapi_application", "on", "JHAAPI Application");

        Assertions.assertTrue(result.isPresent(), "Result must be present.");
        Assertions.assertEquals(state, result.get(), "Result must match the expected.");
    }
}