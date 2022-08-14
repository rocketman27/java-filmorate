package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenresDao;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class GenresDaoImpl implements GenresDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenresDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addGenresForFilm(Film film) {
        if (film.getGenres() != null) {
            String sqlQuery = "INSERT INTO FILMS_GENRES(film_id, genre_id) VALUES (?, ?)";

            film.getGenres().forEach(genre -> jdbcTemplate.update(sqlQuery, film.getId(), genre.getId()));
            log.info("Genres for film with film_id={} have been added", film.getId());
        } else {
            log.info("Cannot set up a genre for filmId={}, genres is null", film.getId());
        }
    }

    @Override
    public List<Long> getGenresByFilmId(long filmId) {
        String sqlQuery = "SELECT genre_id FROM films_genres WHERE film_id = ? ORDER BY genre_id";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, filmId);
    }

    @Override
    public void deleteGenresForFilm(long filmId) {
        String sqlQuery = "DELETE FROM films_genres WHERE film_id = ?";

        int updatedRows = jdbcTemplate.update(sqlQuery, filmId);

        if (updatedRows == 1) {
            log.info("Genres for film with film_id={} have been deleted", filmId);
        } else {
            log.info("Cannot delete genres for film_id = {}, the list of genres is empty", filmId);
        }
    }

    @Override
    public Genre getGenreById(long genre_id) {
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, genre_id);
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException(String.format("Genre with genre_id=%s doesn't exist", genre_id));
        }
    }

    @Override
    public List<Genre> getGenres() {
        String sqlQuery = "SELECT * FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getLong("genre_id"),
                resultSet.getString("genre_name"));
    }
}
