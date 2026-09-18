package com.wynnventory.api.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unwraps the /api/v2 envelopes: every success body is {"data": ...}, every failure body is
 * {"error": {"code", "message"}}.
 */
public final class ApiEnvelope {
    private ApiEnvelope() {}

    /** Returns the {@code data} node of a v2 success body, or null when the body carries none. */
    public static JsonNode data(ObjectMapper mapper, String body) throws JsonProcessingException {
        if (body == null || body.isBlank()) return null;

        JsonNode data = mapper.readTree(body).get("data");
        return data == null || data.isNull() ? null : data;
    }

    /** Returns the parsed v2 error of a failure body, or null when the body is not a v2 error. */
    public static ApiError error(ObjectMapper mapper, String body) {
        if (body == null || body.isBlank()) return null;

        try {
            JsonNode error = mapper.readTree(body).get("error");
            if (error == null || !error.isObject()) return null;

            return new ApiError(
                    error.path("code").asText(null), error.path("message").asText(null));
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
