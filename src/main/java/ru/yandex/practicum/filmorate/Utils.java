package ru.yandex.practicum.filmorate;

public class Utils {
    private static int lastUserId = 0;
    private static int lastFilmId = 0;

    public static int generateFilmId() {
        lastUserId++;
        return lastUserId;
    }

    public static int generateUserId() {
        lastFilmId++;
        return lastFilmId;
    }
}
