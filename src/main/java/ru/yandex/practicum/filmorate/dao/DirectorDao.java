package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.models.Director;

import java.util.Collection;
import java.util.List;

public interface DirectorDao {
    boolean addDirectorsForFilm(long filmId, Collection<Director> directors);

    List<Director> getDirectorsByFilmId(long filmId);

    boolean deleteDirectorsForFilm(long filmId);

    Director getDirectorById(long id);

    List<Director> getDirectors();

    Director add(Director director);

    Director update(Director director);

    boolean deleteById(long directorId);
}
