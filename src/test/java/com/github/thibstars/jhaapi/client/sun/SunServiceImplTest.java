package com.github.thibstars.jhaapi.client.sun;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.sun.response.Sun;
import com.github.thibstars.jhaapi.client.sun.response.SunAttributes;
import java.io.IOException;
import java.util.Optional;
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
class SunServiceImplTest {

    private Call call;
    private Response response;
    private ResponseBody responseBody;
    private SunServiceImpl sunService;

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

        sunService = new SunServiceImpl(configuration);
    }

    @Test
    void shouldGetSunInfo() throws IOException {
        String json = """
                {
                  "attributes": {
                    "next_dawn": "2024-03-20T05:00:00+00:00",
                    "next_dusk": "2024-03-20T19:00:00+00:00",
                    "next_midnight": "2024-03-21T00:00:00+00:00",
                    "next_noon": "2024-03-20T12:00:00+00:00",
                    "next_rising": "2024-03-20T06:00:00+00:00",
                    "next_setting": "2024-03-20T18:00:00+00:00",
                    "elevation": 15.5,
                    "azimuth": 180.0,
                    "rising": true,
                    "friendly_name": "Sun"
                  },
                  "entity_id": "sun.sun",
                  "last_changed": "2024-03-20T10:00:00+00:00",
                  "state": "above_horizon"
                }
                """;

        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(responseBody.string()).thenReturn(json);

        Optional<Sun> sunInfo = sunService.getSunInfo();

        Assertions.assertTrue(sunInfo.isPresent());
        Sun sun = sunInfo.get();
        Assertions.assertEquals("sun.sun", sun.entityId());
        Assertions.assertEquals("above_horizon", sun.state());
        Assertions.assertTrue(sun.isAboveHorizon());
        Assertions.assertFalse(sun.isBelowHorizon());
        
        SunAttributes attributes = sun.attributes();
        Assertions.assertNotNull(attributes);
        Assertions.assertEquals(15.5, attributes.elevation());
        Assertions.assertEquals(180.0, attributes.azimuth());
        Assertions.assertTrue(attributes.rising());
        Assertions.assertEquals("Sun", attributes.friendlyName());
        Assertions.assertNotNull(attributes.nextRising());
        Assertions.assertNotNull(attributes.nextSetting());
    }

    @Test
    void shouldReturnEmptyWhenNotFound() throws IOException {
        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(response.isSuccessful()).thenReturn(false);

        Optional<Sun> sunInfo = sunService.getSunInfo();

        Assertions.assertTrue(sunInfo.isEmpty());
    }
}
