package com.github.thibstars.jhaapi.client.updates;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.updates.response.Update;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class UpdateServiceImplTest {

    private Call call;
    private Response response;
    private ResponseBody responseBody;
    private UpdateServiceImpl updateService;

    @BeforeEach
    void setUp() {
        Configuration configuration = Mockito.mock(Configuration.class);
        OkHttpClient okHttpClient = Mockito.mock(OkHttpClient.class);
        call = Mockito.mock(Call.class);
        response = Mockito.mock(Response.class);
        responseBody = Mockito.mock(ResponseBody.class);

        Mockito.when(configuration.getOkHttpClient()).thenReturn(okHttpClient);
        Mockito.when(configuration.getObjectMapper()).thenReturn(new Configuration("token").getObjectMapper());
        Mockito.when(configuration.getBaseUrl()).thenReturn(new Configuration("token").getBaseUrl());
        Mockito.when(okHttpClient.newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);

        updateService = new UpdateServiceImpl(configuration);
    }

    @Test
    void shouldGetAvailableUpdates() throws IOException {
        String json = """
                [
                  {
                    "attributes": {
                      "installed_version": "1.0.0",
                      "latest_version": "1.1.0",
                      "friendly_name": "Update 1"
                    },
                    "entity_id": "update.update_1",
                    "state": "on"
                  },
                  {
                    "attributes": {
                      "installed_version": "2.0.0",
                      "latest_version": "2.0.0",
                      "friendly_name": "Update 2"
                    },
                    "entity_id": "update.update_2",
                    "state": "off"
                  },
                  {
                    "attributes": {
                      "friendly_name": "Not an update"
                    },
                    "entity_id": "sensor.not_an_update",
                    "state": "on"
                  }
                ]
                """;

        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(responseBody.string()).thenReturn(json);

        List<Update> availableUpdates = updateService.getAvailableUpdates();

        Assertions.assertEquals(1, availableUpdates.size());
        Update update = availableUpdates.getFirst();
        Assertions.assertEquals("update.update_1", update.entityId());
        Assertions.assertEquals("on", update.state());
        Assertions.assertTrue(update.isUpdateAvailable());
        Assertions.assertEquals("1.0.0", update.attributes().installedVersion());
        Assertions.assertEquals("1.1.0", update.attributes().latestVersion());
    }

    @Test
    void shouldReturnEmptyWhenNoneAvailable() throws IOException {
        String json = "[]";

        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(responseBody.string()).thenReturn(json);

        List<Update> availableUpdates = updateService.getAvailableUpdates();

        Assertions.assertTrue(availableUpdates.isEmpty());
    }
}
