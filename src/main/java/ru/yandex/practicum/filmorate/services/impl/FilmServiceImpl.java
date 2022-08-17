package ru.yandex.practicum.filmorate.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmDao filmDao;
    private final MpaDao mpaDao;
    private final GenresDao genresDao;
    private final LikesDao likesDao;
    private final UserDao userDao;

    @Autowired
    public FilmServiceImpl(FilmDao filmDao, MpaDao mpaDao, GenresDao genresDao, LikesDao likesDao, UserDao userDao) {
        this.filmDao = filmDao;
        this.mpaDao = mpaDao;
        this.genresDao = genresDao;
        this.likesDao = likesDao;
        this.userDao = userDao;
    }

    @Override
    public Film addFilm(Film film) {
        log.info("Received request to add film with id={}", film.getId());

        Mpa mpa = mpaDao.getMpaById(film.getMpa()
                                        .getId());
        film.setMpa(mpa);

        film = filmDao.addFilm(film);
        genresDao.addGenresForFilm(film);
        log.info("Film with id={} has been successfully added", film.getId());
        return film;
    }

    @Override
    public List<Film> getFilms() {
        log.info("Received request to get all the films");
        return filmDao.getFilms();
    }

    @Override
    public Film getFilmById(long id) {
        log.info("Received request to get the film with id={}", id);
        Film film = filmDao.getFilmById(id);
        long mpaId = film.getMpa().getId();
        Mpa mpa = mpaDao.getMpaById(mpaId);

        film.setMpa(mpa);

        List<Long> genresIds = genresDao.getGenresByFilmId(id);
        List<Genre> genres = genresIds.stream()
                                      .map(genresDao::getGenreById)
                                      .collect(Collectors.toList());

        film.setGenres(genres);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        log.info("Received request to get a list of popular films");
        return filmDao.getPopularFilms(count);
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        log.info("Received request to get a list of common movies of users with ID " + userId + " and " + friendId);
        return filmDao.getCommonFilms(userId, friendId);
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("Received request to update the film with id={}", film.getId());
        filmDao.updateFilm(film);
        genresDao.deleteGenresForFilm(film.getId());
        removeGenreDuplicates(film);
        genresDao.addGenresForFilm(film);
        return film;
    }

    @Override
    public void addLike(long filmId, long userId) {
        log.info("Received request to add a like by userId={}, for filmId={}", userId, filmId);
        likesDao.addLike(userId, filmId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        log.info("Received request to delete a like by userId={}, for filmId={}", userId, filmId);
        likesDao.deleteLike(userId, filmId);
    }

    private void removeGenreDuplicates(Film film) {
        if (film.getGenres() != null) {
            Set<Genre> uniqueGenres = new TreeSet<>(Comparator.comparing(Genre::getId));
            uniqueGenres.addAll(film.getGenres());
            film.setGenres(new ArrayList<>(uniqueGenres));
        }
    }
}
