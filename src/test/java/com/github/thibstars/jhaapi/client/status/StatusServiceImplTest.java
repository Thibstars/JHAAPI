package com.github.thibstars.jhaapi.client.status;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.status.response.Status;
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
class StatusServiceImplTest {

    @Test
    void shouldGetStatus() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://homeassistant:8123/api/").toURL());

        Call call = Mockito.mock(Call.class);
        Response response = Mockito.mock(Response.class);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        Mockito.when(responseBody.string()).thenReturn("""
                {
                  "message": "API running."
                }
                """);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(configuration.getOkHttpClient().newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);

        Status status = new Status("API running.");
        Mockito.when(configuration.getObjectMapper().readValue(responseBody.string(), Status.class)).thenReturn(status);

        Status result = new StatusServiceImpl(configuration).getStatus().orElseThrow();

        Assertions.assertNotNull(result, "Result must not be null.");
        Assertions.assertEquals(status, result, "Result must match the expected.");
    }
}