package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.models.Mpa;

import java.util.List;

public interface MpaService {

    List<Mpa> getMpas();

    Mpa getMpaById(long id);
}
