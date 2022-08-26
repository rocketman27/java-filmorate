package ru.yandex.practicum.filmorate.models;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder(setterPrefix = "with")
public class Event {
    private long eventId;
    private long userId;
    private long entityId;
    private EventType eventType;
    private OperationType operation;
    private long timestamp;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("event_id", eventId);
        values.put("user_id", userId);
        values.put("entity_id", entityId);
        values.put("event_type", eventType);
        values.put("operation_type", operation);
        values.put("created_at", Timestamp.from(Instant.ofEpochMilli(timestamp)));
        return values;
    }
}
