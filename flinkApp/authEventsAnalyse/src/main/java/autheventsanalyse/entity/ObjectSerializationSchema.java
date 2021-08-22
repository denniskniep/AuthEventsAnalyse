package autheventsanalyse.entity;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectSerializationSchema<T> implements SerializationSchema<T> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(T t) {
        try {
            return objectMapper.writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can not serialize object!");
        }
    }
}
