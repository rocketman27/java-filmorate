package ru.yandex.practicum.filmorate.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenresDao;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.services.GenreService;

import java.util.List;

@Slf4j
@Service
public class GenreServiceImpl implements GenreService {
    private final GenresDao genresDao;

    @Autowired
    public GenreServiceImpl(GenresDao genresDao) {
        this.genresDao = genresDao;
    }

    @Override
    public List<Genre> getGenres() {
        log.info("Received request to get all genres");
        return genresDao.getGenres();
    }

    @Override
    public Genre getGenreById(long id) {
        log.info("Received request to get a genre by genreId={}", id);
        return genresDao.getGenreById(id);
    }
}
