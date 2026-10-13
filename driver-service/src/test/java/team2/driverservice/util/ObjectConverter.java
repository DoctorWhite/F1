package team2.driverservice.util;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

public class ObjectConverter {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    public static <T> T convertJsonToObject(String serializedObject, Class<T> objectClass) {
        return JSON_MAPPER.readValue(serializedObject, objectClass);
    }

    public static String convertObjectToJson(Object object) {
        return JSON_MAPPER.writeValueAsString(object);
    }
}
