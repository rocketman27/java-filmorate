package ru.yandex.practicum.filmorate.services;

import org.springframework.data.relational.core.sql.In;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Score;

import java.util.List;

public interface FilmService {
    Film addFilm(Film film);

    List<Film> getFilms();

    Film getFilmById(long id);

    List<Film> getPopularFilms(int count, Long genreId, Integer year);

    List<Film> getCommonFilms(int userId, int friendId);

    List<Film> getSearch(String query, List<String> by);

    Film updateFilm(Film film);

    void addLike(long filmId, long userId, Score score);

    void deleteLike(long filmId, long userId);

    List<Film> getFilmsByDirectorId(long directorId, String sortBy);

    void removeFilm(long filmId);
}
