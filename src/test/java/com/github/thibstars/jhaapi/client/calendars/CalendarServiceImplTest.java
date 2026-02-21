package com.github.thibstars.jhaapi.client.calendars;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.calendars.response.Calendar;
import com.github.thibstars.jhaapi.client.calendars.response.CalendarEvent;
import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class CalendarServiceImplTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    @Test
    void shouldGetCalendars() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class);
        OkHttpClient okHttpClient = Mockito.mock(OkHttpClient.class);
        Mockito.when(configuration.getOkHttpClient()).thenReturn(okHttpClient);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://localhost:8123/api").toURL());
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);

        Call call = Mockito.mock(Call.class);
        Mockito.when(okHttpClient.newCall(Mockito.any())).thenReturn(call);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.code()).thenReturn(200);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        Mockito.when(responseBody.string()).thenReturn("[{\"entity_id\": \"calendar.personal\", \"name\": \"Personal\"}]");
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);

        CalendarService calendarService = new CalendarServiceImpl(configuration);
        List<Calendar> calendars = calendarService.getCalendars();

        Assertions.assertNotNull(calendars);
        Assertions.assertEquals(1, calendars.size());
        Assertions.assertEquals("calendar.personal", calendars.getFirst().entityId());
    }

    @Test
    void shouldGetCalendarEvents() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class);
        OkHttpClient okHttpClient = Mockito.mock(OkHttpClient.class);
        Mockito.when(configuration.getOkHttpClient()).thenReturn(okHttpClient);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://localhost:8123/api").toURL());
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);

        Call call = Mockito.mock(Call.class);
        Mockito.when(okHttpClient.newCall(Mockito.any())).thenReturn(call);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.code()).thenReturn(200);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        Mockito.when(responseBody.string()).thenReturn("[{\"summary\": \"Meeting\", \"start\": \"2023-10-01T10:00:00Z\", \"end\": \"2023-10-01T11:00:00Z\"}]");
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);

        CalendarService calendarService = new CalendarServiceImpl(configuration);
        OffsetDateTime start = OffsetDateTime.parse("2023-10-01T00:00:00Z");
        OffsetDateTime end = OffsetDateTime.parse("2023-10-02T00:00:00Z");
        List<CalendarEvent> events = calendarService.getCalendarEvents("calendar.personal", start, end);

        Assertions.assertNotNull(events);
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("Meeting", events.getFirst().summary());

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        Mockito.verify(okHttpClient, Mockito.atLeastOnce()).newCall(requestCaptor.capture());
        
        boolean foundUrl = false;
        for (Request request : requestCaptor.getAllValues()) {
            String requestUrlAsString = request.url().toString();
            if (requestUrlAsString.contains("calendars/calendar.personal")) {
                Assertions.assertTrue(requestUrlAsString.contains("start=2023-10-01T00%3A00Z") || requestUrlAsString.contains("start=2023-10-01T00:00Z"));
                Assertions.assertTrue(requestUrlAsString.contains("end=2023-10-02T00%3A00Z") || requestUrlAsString.contains("end=2023-10-02T00:00Z"));
                foundUrl = true;
                break;
            }
        }
        Assertions.assertTrue(foundUrl);
    }
}
