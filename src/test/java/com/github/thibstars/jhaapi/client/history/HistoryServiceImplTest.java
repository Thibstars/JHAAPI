package com.github.thibstars.jhaapi.client.history;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thibstars.jhaapi.Configuration;
import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        List<List<StateChange>> history = List.of(
                List.of(
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
                )
        );
        ObjectMapper objectMapper = configuration.getObjectMapper();
        Mockito.when(objectMapper.readValue(responseBodyString,
                objectMapper.getTypeFactory().constructCollectionType(List.class, objectMapper.getTypeFactory().constructCollectionType(List.class, StateChange.class))))
                .thenReturn(history);

        List<List<StateChange>> result = new HistoryServiceImpl(configuration).getHistory(Set.of("sun.sun"));

        Assertions.assertNotNull(result, "Result must not be null.");
        Assertions.assertNotNull(result.getFirst(), "Sub list must not be null.");
        Assertions.assertEquals(history, result, "Sub list must match the expected.");
    }

    @Test
    void shouldNotGetHistoryWhenNoEntityIdsAreProvided() {
        Configuration configuration = Mockito.mock(Configuration.class, Mockito.RETURNS_DEEP_STUBS);

        HistoryServiceImpl historyService = new HistoryServiceImpl(configuration);
        Assertions.assertThrows(IllegalArgumentException.class, () -> historyService.getHistory(null), "At least 1 entity id must be provided.");
        Set<String> emptySet = new HashSet<>();
        Assertions.assertThrows(IllegalArgumentException.class, () -> historyService.getHistory(emptySet), "At least 1 entity id must be provided.");
    }
}