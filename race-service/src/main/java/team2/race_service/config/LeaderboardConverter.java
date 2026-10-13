package team2.race_service.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import team2.race_service.api.Driver;

import java.io.IOException;
import java.util.Map;

@Converter
public class LeaderboardConverter implements AttributeConverter<Map<Integer, Driver>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<Integer, Driver>> TYPE_REF =
            new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(Map<Integer, Driver> attribute) {
        if (attribute == null) return null;
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not serialize leaderboard", e);
        }
    }

    @Override
    public Map<Integer, Driver> convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return MAPPER.readValue(dbData, TYPE_REF);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not deserialize leaderboard", e);
        }
    }
}
