package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.Utils;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(@NotNull Film film) {
        film.setId(Utils.generateFilmId());
        films.put(film.getId(), film);
        log.info("Film with name {} has been successfully added, id {}", film.getName(), film.getId());
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());

    }

    @Override
    public Film getFilmById(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            log.warn("Film with id={} doesn't exist", id);
            throw new FilmNotFoundException(String.format("Film with id=%s doesn't exist", id));
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Film with id {} has been updated", film.getId());
        } else {
            log.warn("Film with id {} doesn't exist", film.getId());
            throw new FilmNotFoundException(String.format("Film with id %s doesn't exist, nothing to update", film.getId()));
        }
        return film;
    }
}
