package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;

public interface FilmDao {
    Film addFilm(Film film);

    List<Film> getFilms();

    Film getFilmById(long id);

    List<Film> getPopularFilms(int limit);

    List<Film> getPopularFilmsByGenre(int limit, long genreId);

    List<Film> getPopularFilmsByYear(int limit, int year);

    List<Film> getPopularFilms(int limit, long genreId, int year);

    List<Film> getCommonFilms(int userId, int friendId);

    void updateFilm(Film film);

    List<Film> getFilmsByDirectorId(long directorId, String sortBy);
}
