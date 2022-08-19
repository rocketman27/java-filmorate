package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;

public interface FilmDao {
    Film addFilm(Film film);

    List<Film> getFilms();

    Film getFilmById(long id);

    List<Film> getPopularFilms(int limit);

    List<Film> getCommonFilms(int userId, int friendId);

    Film updateFilm(Film film);

    List<Film> getFilmsByDirectorId(long directorId, String sortBy);

    void removeFilm(long filmId);
}
