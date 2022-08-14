package ru.yandex.practicum.filmorate.exceptions;

public class LikeNotFoundException extends EntityNotFoundException {

    public LikeNotFoundException(String message) {
        super(message);
    }
}
