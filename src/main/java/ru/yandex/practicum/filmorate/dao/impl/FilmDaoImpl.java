package ru.yandex.practicum.filmorate.dao.impl;

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
                "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                        "f.rating, m.name AS mpa_name " +
                        "FROM films f " +
                        "JOIN mpa m on f.mpa_id = m.mpa_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film getFilmById(long id) {
        String sqlQuery =
                "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                        "f.rating, m.name AS mpa_name " +
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
                        "LEFT JOIN likes l ON f.film_id = l.film_id " +
                        "JOIN mpa m ON f.mpa_id = m.MPA_ID " +
                        "GROUP BY f.film_id " +
                        "ORDER BY f.rating DESC, COUNT(l.user_id) DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, limit);
    }

    @Override
    public List<Film> getPopularFilmsByGenre(int limit, long genreId) {
        String sqlQuery =
                "SELECT F.*, M.NAME MPA_NAME " +
                        "FROM FILMS F " +
                        "LEFT JOIN LIKES L on F.FILM_ID = L.FILM_ID " +
                        "INNER JOIN MPA M on F.MPA_ID = M.MPA_ID " +
                        "INNER JOIN ( " +
                        "SELECT FILM_ID " +
                        "FROM FILMS_GENRES " +
                        "WHERE GENRE_ID = ? " +
                        ") AS FILMS_BY_GENRE on F.FILM_ID = FILMS_BY_GENRE.FILM_ID " +
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY f.rating DESC, COUNT(l.user_id) DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, genreId, limit);
    }

    @Override
    public List<Film> getPopularFilmsByYear(int limit, int year) {
        String sqlQuery =
                "SELECT F.*, M.NAME MPA_NAME " +
                        "FROM FILMS F " +
                        "LEFT JOIN LIKES L on F.FILM_ID = L.FILM_ID " +
                        "INNER JOIN MPA M on F.MPA_ID = M.MPA_ID " +
                        "WHERE EXTRACT(YEAR FROM F.RELEASE_DATE) = ? " +
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY f.rating DESC, COUNT(l.user_id) DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, year, limit);
    }

    @Override
    public List<Film> getPopularFilms(int limit, long genreId, int year) {
        String sqlQuery =
                "SELECT F.*, M.NAME MPA_NAME " +
                        "FROM FILMS F " +
                        "LEFT JOIN LIKES L on F.FILM_ID = L.FILM_ID " +
                        "INNER JOIN MPA M on F.MPA_ID = M.MPA_ID " +
                        "INNER JOIN ( " +
                        "SELECT FILM_ID " +
                        "FROM FILMS_GENRES " +
                        "WHERE GENRE_ID = ? " +
                        ") AS FILMS_BY_GENRE on F.FILM_ID = FILMS_BY_GENRE.FILM_ID " +
                        "WHERE EXTRACT(YEAR FROM F.RELEASE_DATE) = ? " +
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY f.rating DESC, COUNT(l.user_id) DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, genreId, year, limit);
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sqlQuery =
                "SELECT F.*, M.NAME MPA_NAME " +
                        "FROM FILMS F " +
                        "INNER JOIN MPA M on M.MPA_ID = F.MPA_ID " +
                        "INNER JOIN LIKES L on F.FILM_ID = L.FILM_ID " +
                        "WHERE L.FILM_ID IN ( " +
                        "SELECT FILM_ID " +
                        "FROM LIKES " +
                        "WHERE USER_ID = ?) " +
                        "AND USER_ID = ? " +
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY F.rating DESC, COUNT(L.USER_ID) DESC";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId, friendId);
    }

    @Override
    public List<Film> getRecommendation(long userId) {
        String sqlQuery =
                "SELECT F.*, M.NAME MPA_NAME " +
                        "FROM FILMS F " +
                        "INNER JOIN MPA M ON M.MPA_ID = F.MPA_ID " +
                        "INNER JOIN LIKES L ON F.FILM_ID = L.FILM_ID " +
                        "WHERE L.USER_ID = " +
                        "(SELECT L.USER_ID " +
                        "FROM LIKES L " +
                        "INNER JOIN " +
                        "(SELECT USERS.USER_ID, " +
                        "COUNT( " +
                        "CASE " +
                        "WHEN BAD.FILM_ID IN " +
                        "(SELECT FILM_ID " +
                        "FROM LIKES " +
                        "WHERE USER_ID = ? " +
                        "AND SCORE <= 5) THEN 1 " +
                        "ELSE NULL " +
                        "END) COUNT_FILMS " +
                        "FROM USERS " +
                        "LEFT JOIN ( " +
                        "SELECT USER_ID, FILM_ID " +
                        "FROM LIKES " +
                        "WHERE SCORE <= 5 " +
                        ") AS BAD ON USERS.USER_ID = BAD.USER_ID " +
                        "WHERE USERS.USER_ID <> ? " +
                        "GROUP BY  USERS.USER_ID) AS BAD_FILMS " +
                        "ON L.USER_ID = BAD_FILMS.USER_ID " +
                        "INNER JOIN " +
                        "(SELECT USERS.USER_ID, " +
                        "COUNT( " +
                        "CASE " +
                        "WHEN GOOD.FILM_ID IN " +
                        "(SELECT FILM_ID " +
                        "FROM LIKES " +
                        "WHERE USER_ID = ? " +
                        "AND SCORE >= 6) THEN 1 " +
                        "ELSE NULL " +
                        "END) COUNT_FILMS " +
                        "FROM USERS " +
                        "LEFT JOIN ( " +
                        "SELECT USER_ID, FILM_ID " +
                        "FROM LIKES " +
                        "WHERE SCORE >= 6 " +
                        ") AS GOOD ON USERS.USER_ID = GOOD.USER_ID " +
                        "WHERE USERS.USER_ID <> ? " +
                        "GROUP BY  USERS.USER_ID) AS GOOD_FILMS ON L.USER_ID = GOOD_FILMS.USER_ID " +
                        "GROUP BY  L.USER_ID " +
                        "ORDER BY  (BAD_FILMS.COUNT_FILMS + GOOD_FILMS.COUNT_FILMS) DESC " +
                        "LIMIT 1) " +
                        "AND L.FILM_ID NOT IN " +
                        "(SELECT FILM_ID " +
                        "FROM LIKES " +
                        "WHERE USER_ID = ? " +
                        "OR SCORE < 6)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId, userId, userId, userId, userId);
    }

    @Override
    public List<Film> getSearch(String query) {
        String sqlQuery =
                "SELECT F.*, M.NAME MPA_NAME " +
                        "FROM FILMS F " +
                        "LEFT JOIN LIKES L ON F.FILM_ID = L.FILM_ID " +
                        "INNER JOIN MPA M ON F.MPA_ID = M.MPA_ID " +
                        "LEFT JOIN FILMS_DIRECTORS FD ON F.FILM_ID = FD.FILM_ID " +
                        "LEFT JOIN DIRECTORS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                        "WHERE UPPER(F.NAME) LIKE UPPER(?) " +
                        "OR UPPER(D.NAME) LIKE UPPER(?) " +
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY f.rating DESC, COUNT(L.FILM_ID) DESC";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, "%" + query + "%", "%" + query + "%");
    }

    @Override
    public List<Film> getSearchByDirector(String query) {
        String sqlQuery =
                "SELECT F.*, M.NAME MPA_NAME " +
                        "FROM FILMS F " +
                        "LEFT JOIN LIKES L ON F.FILM_ID = L.FILM_ID " +
                        "INNER JOIN MPA M ON F.MPA_ID = M.MPA_ID " +
                        "LEFT JOIN FILMS_DIRECTORS FD ON F.FILM_ID = FD.FILM_ID " +
                        "LEFT JOIN DIRECTORS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                        "WHERE UPPER(D.NAME) LIKE UPPER(?) " +
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY f.rating DESC, COUNT(L.FILM_ID) DESC";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, "%" + query + "%");
    }

    @Override
    public List<Film> getSearchByTitle(String query) {
        String sqlQuery =
                "SELECT F.*, M.NAME MPA_NAME " +
                        "FROM FILMS F " +
                        "LEFT JOIN LIKES L ON F.FILM_ID = L.FILM_ID " +
                        "INNER JOIN MPA M ON F.MPA_ID = M.MPA_ID " +
                        "WHERE UPPER(F.NAME) LIKE UPPER(?) " +
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY f.rating DESC, COUNT(L.FILM_ID) DESC";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, "%" + query + "%");
    }

    @Override
    public boolean updateFilm(Film film) {
        String sqlQuery =
                "UPDATE films " +
                        "SET film_id = ?, name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                        "WHERE film_id = ?";

        return jdbcTemplate.update(sqlQuery,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()) > 0;
    }

    @Override
    public List<Film> getFilmsByDirectorId(long directorId, String sortBy) {
        String sortByLikesSql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                " f.rating, m.name AS mpa_name, " +
                " count(f.film_id) AS likes_count" +
                " FROM films as f" +
                " INNER JOIN mpa AS m ON f.mpa_id = m.mpa_id" +
                " INNER JOIN films_directors AS fd ON f.film_id = fd.film_id" +
                " LEFT JOIN likes AS l ON f.film_id = l.film_id" +
                " WHERE fd.director_id = ?" +
                " GROUP BY f.film_id" +
                " ORDER by rating DESC, likes_count DESC;";
        String sortByYearSql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                "f.rating, m.name AS mpa_name" +
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

    @Override
    public boolean removeFilm(long filmId) {
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        return jdbcTemplate.update(sqlQuery, filmId) > 0;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                   .withId(resultSet.getLong("film_id"))
                   .withName(resultSet.getString("name"))
                   .withDescription(resultSet.getString("description"))
                   .withReleaseDate(resultSet.getDate("release_date").toLocalDate())
                   .withDuration(resultSet.getInt("duration"))
                   .withRating(resultSet.getFloat("rating"))
                   .withMpa(new Mpa(
                           resultSet.getInt("mpa_id"),
                           resultSet.getString("mpa_name")))
                   .build();
    }
}
