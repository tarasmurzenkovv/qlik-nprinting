package com.nprinting.utils;

import java.io.IOException;

import com.nprinting.model.nprinting.response.Connection;
import com.nprinting.model.nprinting.response.Report;
import com.nprinting.model.nprinting.response.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JsonUtils {

    private static final ObjectMapper INSTANCE = JsonUtils.buildMapper();

    private JsonUtils() {
        throw new RuntimeException("This class should not be instantiated");
    }

    private static ObjectMapper buildMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return mapper;
    }

    public static Response<Connection> deserializeConnection(final String response) {
        try {
            final TypeReference<Response<Connection>> typeReference =
                new TypeReference<Response<Connection>>() {
                };
            return INSTANCE.readValue(response, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Response<Report> deserializeReport(final String response) {
        try {
            final TypeReference<Response<Report>> typeReference =
                new TypeReference<Response<Report>>() {
                };
            return INSTANCE.readValue(response, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String toJson(T object) {
        try {
            return INSTANCE.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readValue(final String json, Class<T> value) {
        try {
            return INSTANCE.readValue(json, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
