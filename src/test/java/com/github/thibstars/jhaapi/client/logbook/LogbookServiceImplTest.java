package com.github.thibstars.jhaapi.client.logbook;


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
class LogbookServiceImplTest {

    @Test
    void shouldGetLogbook() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://homeassistant:8123/api/").toURL());

        Call call = Mockito.mock(Call.class);
        Response response = Mockito.mock(Response.class);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        Mockito.when(responseBody.string()).thenReturn("""
                [
                  {
                    "context_user_id": null,
                    "domain": "alarm_control_panel",
                    "entity_id": "alarm_control_panel.area_001",
                    "message": "changed to disarmed",
                    "name": "Security",
                    "when": "2020-06-20T16:44:26.127295+00:00"
                  },
                  {
                    "context_user_id": null,
                    "domain": "homekit",
                    "entity_id": "alarm_control_panel.area_001",
                    "message": "send command alarm_arm_night for Security",
                    "name": "HomeKit",
                    "when": "2020-06-21T02:59:05.759645+00:00"
                  },
                  {
                    "context_user_id": null,
                    "domain": "alarm_control_panel",
                    "entity_id": "alarm_control_panel.area_001",
                    "message": "changed to armed_night",
                    "name": "Security",
                    "when": "2020-06-21T02:59:06.015463+00:00"
                  }
                ]
                """);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(configuration.getOkHttpClient().newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);

        List<Log> logbook = List.of(
                new Log(null, "alarm_control_panel", "alarm_control_panel.area_001", "changed to disarmed", "Security", OffsetDateTime.parse("2020-06-20T16:44:26.127295+00:00")),
                new Log(null, "homekit", "alarm_control_panel.area_001", "send command alarm_arm_night for Security", "HomeKit", OffsetDateTime.parse("2020-06-21T02:59:05.759645+00:00")),
                new Log(null, "alarm_control_panel", "alarm_control_panel.area_001", "changed to armed_night", "Security", OffsetDateTime.parse("2020-06-21T02:59:06.015463+00:00"))
        );
        ObjectMapper objectMapper = configuration.getObjectMapper();
        Mockito.when(objectMapper.readValue(responseBody.string(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Log.class))).thenReturn(logbook);

        LogbookServiceImpl logbookService = new LogbookServiceImpl(configuration);
        List<Log> result = logbookService.getLogs();

        Assertions.assertNotNull(result, "Result must not be null.");
        Assertions.assertEquals(logbook, result, "Result must match the expected.");
    }
}