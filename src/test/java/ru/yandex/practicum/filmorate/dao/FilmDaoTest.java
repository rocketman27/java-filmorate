package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmDaoTest {
    private final FilmDaoImpl filmDao;

    @Autowired
    public FilmDaoTest(FilmDaoImpl filmDao) {
        this.filmDao = filmDao;
    }

    @Order(1)
    @Test
    void getFilmById() {
        Film film = filmDao.getFilmById(2);

        Assertions.assertNotNull(film);
        Assertions.assertEquals(2, film.getId());
        Assertions.assertEquals("TEST", film.getName());
        Assertions.assertEquals("DESCRIPTION", film.getDescription());
        Assertions.assertEquals(120, film.getDuration());
        Assertions.assertEquals(LocalDate.of(2022, 8, 10), film.getReleaseDate());
        Assertions.assertEquals(1, film.getMpa().getId());
    }

    @Order(2)
    @Test
    void addFilmTest() {
        Film film = Film.builder()
                        .withName("Test")
                        .withDescription("Test")
                        .withDuration(100)
                        .withReleaseDate(LocalDate.of(2022, 8, 10))
                        .withMpa(new Mpa(1, "G"))
                        .withRating(0F)
                        .build();

        film = filmDao.addFilm(film);

        Film addedFilm = filmDao.getFilmById(film.getId());

        Assertions.assertEquals(film, addedFilm);
    }

    @Order(3)
    @Test
    void getFilms() {
        Assertions.assertEquals(3, filmDao.getFilms().size());
    }

    @Order(4)
    @Test
    void updateFilm() {
        Film film = Film.builder()
                        .withId(2)
                        .withName("Test update")
                        .withDescription("Description update")
                        .withDuration(101)
                        .withReleaseDate(LocalDate.of(2022, 7, 10))
                        .withMpa(new Mpa(1, "G"))
                        .withRating(0F)
                        .build();

        filmDao.updateFilm(film);

        Film updatedFilm = filmDao.getFilmById(2);

        Assertions.assertNotNull(updatedFilm);
        Assertions.assertEquals(2, updatedFilm.getId());
        Assertions.assertEquals("Test update", updatedFilm.getName());
        Assertions.assertEquals("Description update", updatedFilm.getDescription());
        Assertions.assertEquals(101, updatedFilm.getDuration());
        Assertions.assertEquals(LocalDate.of(2022, 7, 10), updatedFilm.getReleaseDate());
        Assertions.assertEquals(1, updatedFilm.getMpa().getId());
    }

    @Order(5)
    @Test
    void getPopularFilms() {
        List<Film> popularFilms = filmDao.getPopularFilms(2);

        Assertions.assertEquals(2, popularFilms.size());
    }
}
