package ru.yandex.practicum.filmorate.exceptions;

public class DirectorNotFoundException extends EntityNotFoundException {
    public DirectorNotFoundException(final String message) {
        super(message);
    }
}
