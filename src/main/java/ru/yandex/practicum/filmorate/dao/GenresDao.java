package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;

import java.util.List;

public interface GenresDao {
    void addGenresForFilm(Film film);

    List<Long> getGenresByFilmId(long filmId);

    void deleteGenresForFilm(long filmId);

    Genre getGenreById(long id);

    List<Genre> getGenres();
}
