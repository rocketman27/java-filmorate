package ru.yandex.practicum.filmorate.exceptions;

public class ReviewNotFoundException extends EntityNotFoundException{
    public ReviewNotFoundException(String message) {
        super(message);
    }
}
