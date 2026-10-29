package com.github.thibstars.jhaapi.internal.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.NullNode;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class EventTimeDeserializerTest {

    private EventTimeDeserializer deserializer;

    private JsonParser parser;

    private DeserializationContext context;

    private ObjectCodec codec;

    @BeforeEach
    void setUp() {
        deserializer = new EventTimeDeserializer();
        parser = Mockito.mock(JsonParser.class);
        context = Mockito.mock(DeserializationContext.class);
        codec = Mockito.mock(ObjectCodec.class);
        Mockito.when(parser.getCodec()).thenReturn(codec);
    }

    @Test
    void shouldReturnNullWhenNodeIsNull() throws IOException {
        Mockito.when(codec.readTree(parser)).thenReturn(null);

        OffsetDateTime result = deserializer.deserialize(parser, context);

        Assertions.assertNull(result);
    }

    @Test
    void shouldReturnNullWhenNodeIsJsonNull() throws IOException {
        Mockito.when(codec.readTree(parser)).thenReturn(NullNode.getInstance());

        OffsetDateTime result = deserializer.deserialize(parser, context);

        Assertions.assertNull(result);
    }

    @Test
    void shouldDeserializeTextualIsoDateTime() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("\"2023-10-01T14:30:00+02:00\"");
        Mockito.when(codec.readTree(parser)).thenReturn(node);

        OffsetDateTime result = deserializer.deserialize(parser, context);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(OffsetDateTime.parse("2023-10-01T14:30:00+02:00"), result);
    }

    @Test
    void shouldDeserializeTextualDateOnly() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("\"2023-10-01\"");
        Mockito.when(codec.readTree(parser)).thenReturn(node);

        OffsetDateTime result = deserializer.deserialize(parser, context);

        Assertions.assertNotNull(result);
        OffsetDateTime expected = LocalDate.parse("2023-10-01").atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldDeserializeObjectWithDateTimeField() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{\"dateTime\": \"2023-10-01T10:00:00Z\"}");
        Mockito.when(codec.readTree(parser)).thenReturn(node);

        OffsetDateTime result = deserializer.deserialize(parser, context);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(OffsetDateTime.parse("2023-10-01T10:00:00Z"), result);
    }

    @Test
    void shouldDeserializeObjectWithDateOnlyField() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{\"date\": \"2023-10-01\"}");
        Mockito.when(codec.readTree(parser)).thenReturn(node);

        OffsetDateTime result = deserializer.deserialize(parser, context);

        Assertions.assertNotNull(result);
        OffsetDateTime expected = LocalDate.parse("2023-10-01").atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldDeserializeObjectWithDateFieldHavingFullDateTime() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{\"date\": \"2023-10-01T12:00:00Z\"}");
        Mockito.when(codec.readTree(parser)).thenReturn(node);

        OffsetDateTime result = deserializer.deserialize(parser, context);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(OffsetDateTime.parse("2023-10-01T12:00:00Z"), result);
    }

    @Test
    void shouldReturnNullWhenObjectLacksDateAndDateTime() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{\"foo\": \"bar\"}");
        Mockito.when(codec.readTree(parser)).thenReturn(node);

        OffsetDateTime result = deserializer.deserialize(parser, context);

        Assertions.assertNull(result);
    }

    @Test
    void shouldReturnNullWhenDateAndDateTimeAreNull() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{\"dateTime\": null, \"date\": null}");
        Mockito.when(codec.readTree(parser)).thenReturn(node);

        OffsetDateTime result = deserializer.deserialize(parser, context);

        Assertions.assertNull(result);
    }

    private record TestContainer(
            @JsonDeserialize(using = EventTimeDeserializer.class)
            OffsetDateTime time
    ) {

    }

    @Test
    void shouldDeserializeUsingObjectMapperAnnotation() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        TestContainer timed = mapper.readValue("{\"time\": {\"dateTime\": \"2023-10-01T15:00:00Z\"}}", TestContainer.class);
        Assertions.assertNotNull(timed.time());
        Assertions.assertEquals(OffsetDateTime.parse("2023-10-01T15:00:00Z"), timed.time());

        TestContainer allDay = mapper.readValue("{\"time\": {\"date\": \"2023-10-01\"}}", TestContainer.class);
        Assertions.assertNotNull(allDay.time());
        Assertions.assertEquals(LocalDate.parse("2023-10-01").atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime(), allDay.time());

        TestContainer textual = mapper.readValue("{\"time\": \"2023-10-01T15:00:00Z\"}", TestContainer.class);
        Assertions.assertNotNull(textual.time());
        Assertions.assertEquals(OffsetDateTime.parse("2023-10-01T15:00:00Z"), textual.time());

        TestContainer nullTime = mapper.readValue("{\"time\": null}", TestContainer.class);
        Assertions.assertNull(nullTime.time());
    }
}
