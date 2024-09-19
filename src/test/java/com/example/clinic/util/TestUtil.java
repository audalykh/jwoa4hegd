package com.example.clinic.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.test.web.servlet.MvcResult;

public class TestUtil {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.findAndRegisterModules();
    }

    @SneakyThrows
    public static String asJsonString(Object object) {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    @SneakyThrows
    public static <T> T fromJsonString(MvcResult mvcResult, Class<T> clazz) {
        String responseString = mvcResult.getResponse().getContentAsString();
        return OBJECT_MAPPER.readValue(responseString, clazz);
    }

    @SneakyThrows
    public static <T> T fromJsonString(MvcResult mvcResult, TypeReference<T> typeReference) {
        String responseString = mvcResult.getResponse().getContentAsString();
        return OBJECT_MAPPER.readValue(responseString, typeReference);
    }
}