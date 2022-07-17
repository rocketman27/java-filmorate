package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.LikeNotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);

        if (film == null) {
            log.warn("Film with id={} doesn't exist, cannot add like", filmId);
            throw new FilmNotFoundException(String.format("Film with id=%s doesn't exist, cannot add like", filmId));
        } else {
            film.addLike(userId);
            log.info("Film with id={} received a like from user with id={}", film, userId);
        }
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.warn("Film with id={} doesn't exist, cannot delete like", filmId);
            throw new FilmNotFoundException(String.format("Film with id=%s doesn't exist, cannot delete like", filmId));
        } else if (!film.getLikes().contains(userId)) {
            log.warn("Film with id={} has no like from user with id={}, cannot delete like", filmId, userId);
            throw new LikeNotFoundException(String.format("Film with id=%s has no like from user with id=%s, cannot delete like", filmId, userId));
        } else {
            film.deleteLike(userId);
            log.info("User with id={} has removed his/her like for film with id={}", userId, filmId);
        }
    }

    public List<Film> getPopularFilms(int count) {
        Comparator<Film> filmLikesSizeComparator = Comparator.comparing(film -> film.getLikes().size());
        Comparator<Film> filmLikesSizeComparatorReversed = filmLikesSizeComparator.reversed();
        return filmStorage.getFilms()
                          .stream()
                          .sorted(filmLikesSizeComparatorReversed)
                          .limit(count)
                          .collect(Collectors.toList());
    }
}
