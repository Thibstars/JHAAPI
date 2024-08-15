package com.github.thibstars.jhaapi.client.history;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thibstars.jhaapi.Configuration;
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
class HistoryServiceImplTest {

    @Test
    void shouldGetHistory() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://homeassistant:8123/api/").toURL());

        Call call = Mockito.mock(Call.class);
        Response response = Mockito.mock(Response.class);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        String responseBodyString = """
                [
                  [
                    {
                      "entity_id": "sun.sun",
                      "state": "above_horizon",
                      "attributes": {
                        "friendly_name": "Sun"
                      },
                      "last_changed": "2024-07-17T12:00:00+00:00",
                      "last_reported": "2024-07-17T12:00:00+00:00",
                      "last_updated": "2024-07-17T12:00:00+00:00",
                      "context": {
                        "id": "01J2EJCPCH01BVZJP660G9JKXA",
                        "parent_id": null,
                        "user_id": null
                      }
                    }
                  ]
                ]
                """;
        Mockito.when(responseBody.string()).thenReturn(responseBodyString);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(configuration.getOkHttpClient().newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);

        List<StateChange> stateChanges = List.of(
                new StateChange(
                        new Attributes(
                                "Sun",
                                null
                        ),
                        "sun.sun",
                        OffsetDateTime.parse("2024-07-17T12:00:00+00:00"),
                        OffsetDateTime.parse("2024-07-17T12:00:00+00:00"),
                        OffsetDateTime.parse("2024-07-17T12:00:00+00:00"),
                        "above_horizon",
                        new Context(
                                "01J2EJCPCH01BVZJP660G9JKXA",
                                null,
                                null
                        )
                )
        );
        ObjectMapper objectMapper = configuration.getObjectMapper();
        Mockito.when(objectMapper.readValue(responseBodyString.substring(1, responseBodyString.length() -1),
                objectMapper.getTypeFactory().constructCollectionType(List.class, StateChange.class))).thenReturn(stateChanges);

        List<StateChange> result = new HistoryServiceImpl(configuration).getHistory(null, "sun.sun");

        Assertions.assertNotNull(result, "Result must not be null.");
        Assertions.assertEquals(stateChanges, result, "Result must match the expected.");
    }
}