package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.models.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component("directorDaoImpl")
public class DirectorDaoImpl implements DirectorDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean addDirectorsForFilm(long filmId, Collection<Director> directors) {
        String params = "(?, ?)";
        String values =  String.join(",", Collections.nCopies(directors.size(), params));
        String sql = String.format("INSERT INTO films_directors (film_id, director_id) VALUES %s", values);
        Object[] flatDirectors = directors.stream()
                .peek(d -> {
                    if (d == null) {
                        throw new DirectorNotFoundException("Director from list can't be null");
                    }
                })
                .map(d -> List.of(filmId, d.getId()))
                .flatMap(Collection::stream)
                .toArray();
        return jdbcTemplate.update(sql, flatDirectors) > 0;
    }

    @Override
    public List<Director> getDirectorsByFilmId(long filmId) {
        String sql = "SELECT DISTINCT d.director_id, d.name FROM directors as d " +
                " INNER JOIN films_directors AS fd ON d.director_id = fd.director_id " +
                " WHERE fd.film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), filmId);
    }

    @Override
    public boolean deleteDirectorsForFilm(long filmId) {
        String sql = "DELETE FROM films_directors WHERE film_id = ?";
        return  jdbcTemplate.update(sql, filmId) > 0;
    }

    @Override
    public Director getDirectorById(long id) {
        String sql = "SELECT director_id, name FROM directors WHERE director_id = ?";
        List<Director> resultQuery = jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), id);
        if (resultQuery.size() != 1) {
            throw new DirectorNotFoundException(String.format("Director with id=%s doesn't exist", id));
        }
        return resultQuery.get(0);
    }

    @Override
    public List<Director> getDirectors() {
        String sql = "SELECT director_id, name FROM directors";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director add(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        long directorId = simpleJdbcInsert.executeAndReturnKey(director.toMap()).longValue();
        director.setId(directorId);
        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE director_id = ?";
        if (jdbcTemplate.update(sql, director.getName(), director.getId()) == 0) {
            throw  new DirectorNotFoundException(String.format("Director with id = %s not found", director.getId()));
        }
        return director;
    }

    @Override
    public boolean deleteById(long directorId) {
        String sql = "DELETE FROM directors WHERE director_id = ?";
        return jdbcTemplate.update(sql, directorId) > 0;
    }

    private Director makeDirector(ResultSet resultSet) throws SQLException {
        return new Director(resultSet.getLong("director_id"), resultSet.getString("name"));
    }
}
