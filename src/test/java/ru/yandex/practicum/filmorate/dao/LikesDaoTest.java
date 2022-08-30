package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Mpa;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

@SpringBootTest
@Sql({"/schema.sql", "/data-test-films.sql", "/data-test-users.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LikesDaoTest {
    private final FilmDao filmDao;
    private final LikesDao likesDao;
    static Film film;

    @Autowired
    public LikesDaoTest(FilmDao filmDao, LikesDao likesDao) {
        this.filmDao = filmDao;
        this.likesDao = likesDao;
    }

    @BeforeAll
    static void init() {
        film = Film.builder()
                .withName("Good film")
                .withDescription("Description")
                .withReleaseDate(LocalDate.of(2000, 01, 01))
                .withMpa(new Mpa(1, "PG-13"))
                .withDuration(120)
                .withRating(0F)
                .build();
    }

    @Test
    void shouldAddAndRecalculateScore() {
        likesDao.addLike(1, 1, 9);
        Film actual = filmDao.getFilmById(1);
        assertThat(actual.getRating()).isNotNull().isEqualTo(9F);
    }
}
