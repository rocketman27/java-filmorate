package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    List<Film> getFilms();

    Film getFilmById(int id);

    Film updateFilm(Film film);
}
