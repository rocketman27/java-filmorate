package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;

public interface FilmService {
    Film addFilm(Film film);

    List<Film> getFilms();

    Film getFilmById(long id);

    List<Film> getPopularFilms(int count, Long genreId, Integer year);

    List<Film> getCommonFilms(int userId, int friendId);

    Film updateFilm(Film film);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<Film> getFilmsByDirectorId(long directorId, String sortBy);

    void removeFilm(long filmId);
}
