package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(filmId);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery =
                "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name " +
                        "FROM films f " +
                        "JOIN mpa m on f.mpa_id = m.mpa_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film getFilmById(long id) {
        String sqlQuery =
                "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name " +
                        "FROM films f " +
                        "JOIN mpa m on f.mpa_id = m.mpa_id " +
                        "WHERE film_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(String.format("Film with id=%s doesn't exist", id));
        }
    }

    @Override
    public List<Film> getPopularFilms(int limit) {
        String sqlQuery =
                "SELECT f.*, m.name AS mpa_name " +
                        "FROM films f " +
                        "LEFT JOIN likes ul ON f.film_id = ul.film_id " +
                        "JOIN mpa m ON f.mpa_id = m.MPA_ID " +
                        "GROUP BY f.film_id " +
                        "ORDER BY COUNT(ul.user_id) DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, limit);
    }

    @Override
    public void updateFilm(Film film) {
        String sqlQuery =
                "UPDATE films " +
                        "SET film_id = ?, name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                        "WHERE film_id = ?";

        int rowsUpdated = jdbcTemplate.update(sqlQuery,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (rowsUpdated == 1) {
            log.info("Film with filmId={} has been updated", film.getId());
        } else {
            throw new FilmNotFoundException(String.format("Film with film_id=%s doesn't exist", film.getId()));
        }
    }

    @Override
    public List<Film> getFilmsByDirectorId(long directorId, String sortBy) {
        String sortByLikesSql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                " m.name AS mpa_name, " +
                " count(f.film_id) AS likes_count" +
                " FROM films as f" +
                " INNER JOIN mpa AS m ON f.mpa_id = m.mpa_id" +
                " INNER JOIN films_directors AS fd ON f.film_id = fd.film_id" +
                " LEFT JOIN likes AS l ON f.film_id = l.film_id" +
                " WHERE fd.director_id = ?" +
                " GROUP BY f.film_id" +
                " ORDER by likes_count DESC;";
        String sortByYearSql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                "m.name AS mpa_name" +
                " FROM films as f" +
                " INNER JOIN mpa AS m ON f.mpa_id = m.mpa_id" +
                " INNER JOIN films_directors AS fd ON f.film_id = fd.film_id" +
                " WHERE fd.director_id = ?" +
                " ORDER by f.release_date;";

        String sql = sortByLikesSql;
        switch (sortBy) {
            case "likes":
                break;
            case "year":
                sql = sortByYearSql;
        }

        return jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .withId(resultSet.getLong("film_id"))
                .withName(resultSet.getString("name"))
                .withDescription(resultSet.getString("description"))
                .withReleaseDate(resultSet.getDate("release_date").toLocalDate())
                .withDuration(resultSet.getInt("duration"))
                .withMpa(new Mpa(
                        resultSet.getInt("mpa_id"),
                        resultSet.getString("mpa_name")))
                .build();
    }
}
