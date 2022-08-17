package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.models.Director;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

@SpringBootTest
@Sql({"/schema.sql", "/data-test-films.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DirectorDaoTest {
    final DirectorDao directorDao;
    final FilmDao filmDao;

    static Director director1, director2, director3;

    @BeforeAll
    static void init() {
        director1 = new Director(1, "Квентин Тарантино");
        director2 = new Director(2, "Лана Вачовски");
        director3 = new Director(3, "Питер Джексон");
    }

    @Autowired
    public DirectorDaoTest(DirectorDao directorDao, FilmDao filmDao) {
        this.directorDao = directorDao;
        this.filmDao = filmDao;
    }

    @Test
    void shouldAddDirector() {
        long directorId = directorDao.add(director1).getId();
        director1.setId(directorId);

        Director actual = directorDao.getDirectorById(directorId);

        assertThat(actual).isNotNull()
                .isEqualTo(director1);
    }

    @Test
    void shouldNotAddNull() {
        Director actual = directorDao.add(null);
        assertThat(actual).isNull();
    }

    @Test
    void shouldGetDirector() {
        long directorId = directorDao.add(director2).getId();
        director2.setId(directorId);

        Director actual = directorDao.getDirectorById(directorId);

        assertThat(actual).isNotNull()
                .isEqualTo(director2);
    }

    @Test
    void shouldGetDirectors() {
        director1 = directorDao.add(director1);
        director2 = directorDao.add(director2);
        director3 = directorDao.add(director3);

        List<Director> actual = directorDao.getDirectors();
        assertThat(actual).isNotNull()
                .hasSize(3)
                .isSubsetOf(List.of(director1, director2, director3));
    }

    @Test
    void shouldNotGetWithIncorrectId() {
        assertThatThrownBy(() -> directorDao.getDirectorById(-1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldUpdateDirector() {
        long directorId = directorDao.add(director1).getId();
        director1.setId(directorId);

        director2.setId(directorId);
        Director actual = directorDao.update(director2);

        assertThat(actual).isNotNull()
                .isEqualTo(director2);
    }

    @Test
    void shouldNotUpdateWithIncorrectId() {
        director1 = directorDao.add(director1);
        director1.setId(-1);

        assertThatThrownBy(() -> directorDao.update(director1))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldDeleteById() {
        director1 = directorDao.add(director1);

        directorDao.deleteById(director1.getId());

        assertThatThrownBy(() -> directorDao.getDirectorById(director1.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldNotDeleteByIncorrectId() {
        director1 = directorDao.add(director1);

        directorDao.deleteById(-1);

        List<Director> actual = directorDao.getDirectors();

        assertThat(actual).isNotNull()
                .hasSize(1)
                .isEqualTo(List.of(director1));
    }

    @Test
    void shouldAddGetDirectorsForFilm() {
        director1 = directorDao.add(director1);

        boolean actual = directorDao.addDirectorsForFilm(1L, List.of(director1));

        assertThat(actual).isTrue();

        List<Director> actualDirectors = directorDao.getDirectorsByFilmId(1L);

        assertThat(actualDirectors).isNotNull()
                .hasSize(1)
                .isEqualTo(List.of(director1));
    }

    @Test
    void shouldNotGetDirectorsForFilmWithIncorrectId() {
        List<Director> actualDirectors = directorDao.getDirectorsByFilmId(-1L);

        assertThat(actualDirectors).isNotNull()
                .hasSize(0);
    }

    @Test
    void shouldNotAddDirectorsForFilmWithIncorrectId() {
        director1 = directorDao.add(director1);

        assertThatThrownBy(() -> directorDao.addDirectorsForFilm(-1L, List.of(director1)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldDeleteDirectorsForFilm() {
        director1 = directorDao.add(director1);
        directorDao.addDirectorsForFilm(1L, List.of(director1));
        boolean actual = directorDao.deleteDirectorsForFilm(1L);

        assertThat(actual).isTrue();

        List<Director> actualDirectors = directorDao.getDirectorsByFilmId(1L);

        assertThat(actualDirectors).isNotNull()
                .hasSize(0);
    }

    @Test
    void shouldNotDeleteDirectorsForFilmWithIncorrectId() {
        boolean actual = directorDao.deleteDirectorsForFilm(-1L);

        assertThat(actual).isFalse();
    }
}
