package com.wynnventory.api.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class ApiEnvelopeTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void dataUnwrapsAnObjectPayload() throws Exception {
        JsonNode data = ApiEnvelope.data(MAPPER, "{\"data\":{\"name\":\"Divzer\"}}");
        assertEquals("Divzer", data.get("name").asText());
    }

    @Test
    void dataUnwrapsAnArrayPayload() throws Exception {
        JsonNode data = ApiEnvelope.data(MAPPER, "{\"data\":[1,2],\"pagination\":{\"page\":1}}");
        assertTrue(data.isArray());
        assertEquals(2, data.size());
    }

    @Test
    void dataIsNullWhenTheEnvelopeIsMissing() throws Exception {
        assertNull(ApiEnvelope.data(MAPPER, "{\"name\":\"Divzer\"}"));
        assertNull(ApiEnvelope.data(MAPPER, "{\"data\":null}"));
        assertNull(ApiEnvelope.data(MAPPER, ""));
        assertNull(ApiEnvelope.data(MAPPER, null));
    }

    @Test
    void dataRejectsMalformedJson() {
        assertThrows(JsonProcessingException.class, () -> ApiEnvelope.data(MAPPER, "{not json"));
    }

    @Test
    void errorParsesTheV2ErrorShape() {
        ApiError error = ApiEnvelope.error(
                MAPPER, "{\"error\":{\"code\":\"not_found\",\"message\":\"No price data\",\"details\":[]}}");
        assertEquals(new ApiError("not_found", "No price data"), error);
    }

    @Test
    void errorIsNullForNonErrorBodies() {
        assertNull(ApiEnvelope.error(MAPPER, "{\"data\":{}}"));
        assertNull(ApiEnvelope.error(MAPPER, "{\"error\":\"Missing API key\"}"));
        assertNull(ApiEnvelope.error(MAPPER, "<html>502 Bad Gateway</html>"));
        assertNull(ApiEnvelope.error(MAPPER, ""));
        assertNull(ApiEnvelope.error(MAPPER, null));
    }
}
