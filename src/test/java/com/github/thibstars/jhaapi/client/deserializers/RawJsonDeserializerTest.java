package com.github.thibstars.jhaapi.client.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class RawJsonDeserializerTest {

    @Test
    void shouldDeserializeRawJson() throws IOException {
        String rawJson = "{\"name\":\"John Doe\"}";

        JsonParser jsonParser = Mockito.mock(JsonParser.class);
        JsonNode jsonNode = Mockito.mock(JsonNode.class);
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(jsonParser.getCodec()).thenReturn(objectMapper);
        Mockito.when(objectMapper.readTree(jsonParser)).thenReturn(jsonNode);
        Mockito.when(objectMapper.writeValueAsString(jsonNode)).thenReturn(rawJson);

        DeserializationContext deserializationContext = Mockito.mock(DeserializationContext.class);
        Mockito.verifyNoInteractions(deserializationContext);

        String result = new RawJsonDeserializer()
                .deserialize(jsonParser, deserializationContext);

        Assertions.assertEquals(
                rawJson,
                result,
                "Raw Json must be returned."
        );
    }
}