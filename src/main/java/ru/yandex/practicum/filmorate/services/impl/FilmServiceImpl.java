package ru.yandex.practicum.filmorate.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenresDao;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.services.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmDao filmDao;
    private final MpaDao mpaDao;
    private final GenresDao genresDao;
    private final LikesDao likesDao;
    private final DirectorDao directorDao;
    private final UserDao userDao;

    @Autowired
    public FilmServiceImpl(FilmDao filmDao, MpaDao mpaDao,
                           GenresDao genresDao, LikesDao likesDao, DirectorDao directorDao, UserDao userDao) {
        this.filmDao = filmDao;
        this.mpaDao = mpaDao;
        this.genresDao = genresDao;
        this.likesDao = likesDao;
        this.directorDao = directorDao;
        this.userDao = userDao;
    }

    @Override
    public Film addFilm(Film film) {
        if (film == null) {
            log.warn("Received request to update the film=null");
            return null;
        }
        log.info("Received request to add film with id={}", film.getId());

        Mpa mpa = mpaDao.getMpaById(film.getMpa().getId());
        film.setMpa(mpa);

        film = filmDao.addFilm(film);
        addGenresForFilm(film.getId(), film.getGenres());
        addDirectorsForFilm(film.getId(), film.getDirectors());
        log.info("Film with id={} has been successfully added", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film == null) {
            log.warn("Received request to update the film=null");
            return null;
        }
        log.info("Received request to update the film with id={}", film.getId());
        filmDao.updateFilm(film);
        genresDao.deleteGenresForFilm(film.getId());
        addGenresForFilm(film.getId(), film.getGenres());
        directorDao.deleteDirectorsForFilm(film.getId());
        addDirectorsForFilm(film.getId(), film.getDirectors());
        return film;
    }

    private boolean addGenresForFilm(long filmId, Collection<Genre> genres) {
        if (genres == null || genres.size() == 0) {
            log.info("Cannot set up a genre for filmId={}, genres is empty", filmId);
            return false;
        }
        return genresDao.addGenresForFilm(filmId, genres);
    }

    private boolean addDirectorsForFilm(long filmId, Collection<Director> directors) {
        if (directors == null || directors.size() == 0) {
            log.info("Cannot set up a directors for filmId={}, directors is empty", filmId);
            return false;
        }
        return directorDao.addDirectorsForFilm(filmId, directors);
    }

    @Override
    public List<Film> getFilms() {
        log.info("Received request to get all the films");
        List<Film> films = filmDao.getFilms();
        films.forEach(f -> {
            f.setGenres(genresDao.getGenresByFilmId(f.getId()));
            f.setDirectors(directorDao.getDirectorsByFilmId(f.getId()));
        });
        return films;
    }

    @Override
    public Film getFilmById(long filmId) {
        log.info("Received request to get the film with id={}", filmId);
        Film film = filmDao.getFilmById(filmId);
        long mpaId = film.getMpa().getId();
        Mpa mpa = mpaDao.getMpaById(mpaId);

        film.setMpa(mpa);

        List<Genre> genres = genresDao.getGenresByFilmId(filmId);
        film.setGenres(genres);
        film.setDirectors(directorDao.getDirectorsByFilmId(filmId));
        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        log.info("Received request to get a list of popular films");
        List<Film> films = filmDao.getPopularFilms(count);
        films.forEach(f -> {
            f.setGenres(genresDao.getGenresByFilmId(f.getId()));
            f.setDirectors(directorDao.getDirectorsByFilmId(f.getId()));
        });
        return films;
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        log.info("Received request to get a list of common films of users with ID " + userId + " and " + friendId);
        userDao.getUserById(userId);
        userDao.getUserById(friendId);
        List<Film> films = filmDao.getCommonFilms(userId, friendId);
        films.forEach(f -> {
            f.setGenres(genresDao.getGenresByFilmId(f.getId()));
            f.setDirectors(directorDao.getDirectorsByFilmId(f.getId()));
        });
        return films;
    }

    @Override
    public List<Film> getFilmsByDirectorId(long directorId, String sortBy) {
        log.info("Received request to get a list of films whit director_id={} and sort by {}",
                directorId, sortBy);
        if (directorDao.getDirectorById(directorId) == null) {
            throw new DirectorNotFoundException(String.format("Director with id=%s doesn't exist", directorId));
        }
        List<Film> films = filmDao.getFilmsByDirectorId(directorId, sortBy);
        films.forEach(f -> {
            f.setGenres(genresDao.getGenresByFilmId(f.getId()));
            f.setDirectors(directorDao.getDirectorsByFilmId(f.getId()));
        });
        return films;
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
}
