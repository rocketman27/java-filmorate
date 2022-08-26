package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.models.Director;

import java.util.List;

public interface DirectorService {
    Director getById(long id);

    List<Director> getAll();

    boolean deleteById(long id);

    Director add(Director director);

    Director update(Director director);
}
