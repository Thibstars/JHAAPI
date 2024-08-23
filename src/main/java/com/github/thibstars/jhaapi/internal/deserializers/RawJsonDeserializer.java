package com.github.thibstars.jhaapi.internal.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 * @author Thibault Helsmoortel
 */
public class RawJsonDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper objectMapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode node = objectMapper.readTree(jsonParser);

        return objectMapper.writeValueAsString(node);
    }
}