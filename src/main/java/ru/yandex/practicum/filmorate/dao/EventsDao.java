package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.models.Event;

import java.util.List;

public interface EventsDao {

    List<Event> getEvents(long userId);

    void addEvent(Event event);
}
