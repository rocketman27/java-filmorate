package ru.yandex.practicum.filmorate.exceptions;

public class FilmNotFoundException extends EntityNotFoundException {

    public FilmNotFoundException(String message) {
        super(message);
    }
}
