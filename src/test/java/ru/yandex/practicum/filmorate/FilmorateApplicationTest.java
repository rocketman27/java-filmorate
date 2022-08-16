package ru.yandex.practicum.filmorate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.GenreController;
import ru.yandex.practicum.filmorate.controllers.MpaController;
import ru.yandex.practicum.filmorate.controllers.UserController;

@SpringBootTest
class FilmorateApplicationTest {

    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;
    @Autowired
    private MpaController mpaController;
    @Autowired
    private GenreController genreController;

    @Test
    public void contextLoads() {
        Assertions.assertThat(filmController).isNotNull();
        Assertions.assertThat(userController).isNotNull();
        Assertions.assertThat(mpaController).isNotNull();
        Assertions.assertThat(genreController).isNotNull();
    }
}
