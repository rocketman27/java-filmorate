package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.models.Genre;

import java.util.List;

public interface GenreService {

    List<Genre> getGenres();

    Genre getGenreById(long id);
}
