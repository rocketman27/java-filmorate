package ru.yandex.practicum.filmorate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.UserController;

@SpringBootTest
class FilmorateApplicationTest {

    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;

    @Test
    public void contextLoads() {
        Assertions.assertThat(filmController).isNotNull();
        Assertions.assertThat(userController).isNotNull();
    }
}
