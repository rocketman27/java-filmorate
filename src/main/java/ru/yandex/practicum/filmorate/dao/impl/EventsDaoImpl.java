package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.EventsDao;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.OperationType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EventsDaoImpl implements EventsDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> getEvents(long userId) {
        String sqlQuery = "SELECT * FROM events WHERE user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToEvent, userId);
    }

    @Override
    public void addEvent(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("event_id");

        long eventId = simpleJdbcInsert.executeAndReturnKey(event.toMap()).longValue();
        event.setEventId(eventId);
    }

    private Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                    .withEventId(resultSet.getLong("event_id"))
                    .withEntityId(resultSet.getLong("entity_id"))
                    .withUserId(resultSet.getLong("user_id"))
                    .withOperation(OperationType.valueOf(resultSet.getString("operation_type")))
                    .withEventType(EventType.valueOf(resultSet.getString("event_type")))
                    .withTimestamp(resultSet.getTimestamp("created_at").getTime())
                    .build();
    }
}
