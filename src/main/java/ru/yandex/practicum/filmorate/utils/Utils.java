package ru.yandex.practicum.filmorate.utils;

public class Utils {
    private static long lastUserId = 0;
    private static long lastFilmId = 0;

    public static long generateFilmId() {
        lastUserId++;
        return lastUserId;
    }

    public static long generateUserId() {
        lastFilmId++;
        return lastFilmId;
    }
}
