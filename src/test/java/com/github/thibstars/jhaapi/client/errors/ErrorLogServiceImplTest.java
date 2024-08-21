package com.github.thibstars.jhaapi.client.errors;

import com.github.thibstars.jhaapi.Configuration;
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
class ErrorLogServiceImplTest {

    @Test
    void shouldGetErrorLog() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://homeassistant:8123/api/").toURL());

        Call call = Mockito.mock(Call.class);
        Response response = Mockito.mock(Response.class);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        String stringErrorLog = """
                15-12-20 11:02:50 homeassistant.components.recorder: Found unfinished sessions
                15-12-20 11:03:03 netdisco.ssdp: Error fetching description at http://192.168.1.1:8200/rootDesc.xml
                15-12-20 11:04:36 homeassistant.components.alexa: Received unknown intent HelpIntent
                """;
        Mockito.when(responseBody.string()).thenReturn(stringErrorLog);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(configuration.getOkHttpClient().newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);

        ErrorLog errorLog = new ErrorLog(stringErrorLog);
        ErrorLog result = new ErrorLogServiceImpl(configuration).getErrorLog().orElseThrow();

        Assertions.assertNotNull(result, "Result must not be null.");
        Assertions.assertEquals(errorLog, result, "Result must match the expected.");
    }
}