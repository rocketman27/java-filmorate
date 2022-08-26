package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.models.Genre;

import java.util.Collection;
import java.util.List;

public interface GenresDao {
    boolean addGenresForFilm(long filmId, Collection<Genre> genres);

    List<Genre> getGenresByFilmId(long filmId);

    boolean deleteGenresForFilm(long filmId);

    Genre getGenreById(long id);

    List<Genre> getGenres();
}
