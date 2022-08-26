package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenresDao;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
public class GenresDaoImpl implements GenresDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenresDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean addGenresForFilm(long filmId, Collection<Genre> genres) {
        String sqlQuery = "INSERT INTO FILMS_GENRES(film_id, genre_id) VALUES (?, ?)";

        genres.forEach(genre -> jdbcTemplate.update(sqlQuery, filmId, genre.getId()));
        return true;
    }

    @Override
    public List<Genre> getGenresByFilmId(long filmId) {
        String sqlQuery = "SELECT DISTINCT g.genre_id, g.name FROM genres AS g" +
                " INNER JOIN films_genres AS fg ON g.genre_id = fg.genre_id" +
                " WHERE film_id = ? ORDER BY genre_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }

    @Override
    public boolean deleteGenresForFilm(long filmId) {
        String sqlQuery = "DELETE FROM films_genres WHERE film_id = ?";
        return jdbcTemplate.update(sqlQuery, filmId) > 0;

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
                resultSet.getString("name"));
    }
}
