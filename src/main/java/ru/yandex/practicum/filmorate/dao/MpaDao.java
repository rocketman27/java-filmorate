package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.models.Mpa;

import java.util.List;

public interface MpaDao {

    Mpa getMpaById(long id);

    List<Mpa> getMpas();
}
