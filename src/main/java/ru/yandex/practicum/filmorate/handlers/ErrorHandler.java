package ru.yandex.practicum.filmorate.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.controllers.GenreController;
import ru.yandex.practicum.filmorate.controllers.MpaController;
import ru.yandex.practicum.filmorate.controllers.DirectorController;
import ru.yandex.practicum.filmorate.controllers.ReviewController;
import ru.yandex.practicum.filmorate.exceptions.DaoException;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.IllegalSearchArgumentException;

import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        FilmController.class,
        UserController.class,
        GenreController.class,
        MpaController.class,
        DirectorController.class,
        ReviewController.class
})
public class ErrorHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFoundException(final EntityNotFoundException e) {
        log.error("Server returned HttpCode 404: {}", e.getMessage(), e);
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DaoException.class)
    public ResponseEntity<Map<String, String>> handleDaoException(final DaoException e) {
        log.error("Server returned HttpCode 500: {}", e.getMessage(), e);
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(IllegalSearchArgumentException.class)
    public ResponseEntity<Map<String, String>> handlerSearchException(final IllegalSearchArgumentException e) {
        log.error("Server returned HttpCode 400: {}", e.getMessage(), e);
        return new ResponseEntity<>(
             Map.of("error", e.getMessage()),
             HttpStatus.BAD_REQUEST
        );
    }
}
