package com.github.thibstars.jhaapi.client.templates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thibstars.jhaapi.Configuration;
import java.io.IOException;
import java.net.URI;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class TemplateServiceImplTest {

    @Test
    void shouldRenderTemplate() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class);
        OkHttpClient okHttpClient = Mockito.mock(OkHttpClient.class);
        Mockito.when(configuration.getOkHttpClient()).thenReturn(okHttpClient);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://localhost:8123/api").toURL());
        Mockito.when(configuration.getObjectMapper()).thenReturn(new ObjectMapper());

        Call call = Mockito.mock(Call.class);
        Mockito.when(okHttpClient.newCall(Mockito.any())).thenReturn(call);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.code()).thenReturn(200);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        Mockito.when(responseBody.string()).thenReturn("Rendered result");
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);

        TemplateService templateService = new TemplateServiceImpl(configuration);
        String result = templateService.renderTemplate("The state of sun is {{ states('sun.sun') }}");

        Assertions.assertEquals("Rendered result", result);

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        Mockito.verify(okHttpClient).newCall(requestCaptor.capture());

        Request capturedRequest = requestCaptor.getValue();
        Assertions.assertEquals("http://localhost:8123/api/template", capturedRequest.url().toString());
        Assertions.assertEquals("POST", capturedRequest.method());
    }
}
