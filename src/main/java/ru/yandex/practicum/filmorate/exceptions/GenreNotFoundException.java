package ru.yandex.practicum.filmorate.exceptions;

public class GenreNotFoundException extends EntityNotFoundException {

    public GenreNotFoundException(String message) {
        super(message);
    }
}
