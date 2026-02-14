package com.github.thibstars.jhaapi.client.services;

import com.github.thibstars.jhaapi.Configuration;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class MediaServiceImplTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldUploadMedia() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class);
        OkHttpClient okHttpClient = Mockito.mock(OkHttpClient.class);
        Mockito.when(configuration.getOkHttpClient()).thenReturn(okHttpClient);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://localhost:8123/api").toURL());

        Call call = Mockito.mock(Call.class);
        Mockito.when(okHttpClient.newCall(Mockito.any())).thenReturn(call);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.code()).thenReturn(200);
        Mockito.when(call.execute()).thenReturn(response);

        MediaServiceImpl mediaService = new MediaServiceImpl(configuration);

        Path testFile = tempDir.resolve("test.jpg");
        Files.writeString(testFile, "test content");
        File file = testFile.toFile();

        mediaService.uploadMedia(file, "local");

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        Mockito.verify(okHttpClient).newCall(requestCaptor.capture());

        Request capturedRequest = requestCaptor.getValue();
        Assertions.assertEquals("http://localhost:8123/api/media_source/local_source/upload", capturedRequest.url().toString());
        Assertions.assertEquals("POST", capturedRequest.method());
        Assertions.assertNotNull(capturedRequest.body());
        Assertions.assertTrue(capturedRequest.body() instanceof MultipartBody);
        MultipartBody body = (MultipartBody) capturedRequest.body();
        Assertions.assertEquals(2, body.parts().size());
        
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        String bodyString = buffer.readUtf8();
        Assertions.assertTrue(bodyString.contains("name=\"media_content_id\""));
        Assertions.assertTrue(bodyString.contains("media-source://media_source/local/."));
        Assertions.assertTrue(bodyString.contains("name=\"file\""));
        Assertions.assertTrue(bodyString.contains("filename=\"test.jpg\""));
    }
}
