package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.models.Director;

import java.util.List;

@Slf4j
@Service("directorServiceImpl")
public class DirectorServiceImpl implements DirectorService {
    private final DirectorDao directorDao;

    @Autowired
    public DirectorServiceImpl(@Qualifier("directorDBDao") DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    @Override
    public Director getById(long directorId) {
        log.info("Received request to get director by id = {}", directorId);
        return directorDao.getDirectorById(directorId);
    }

    @Override
    public List<Director> getAll() {
        log.info("Received request to get all directors");
        return directorDao.getDirectors();
    }

    @Override
    public boolean deleteById(long directorId) {
        log.info("Received request to delete director with id={}", directorId);
        boolean result = directorDao.deleteById(directorId);
        if (result) {
            log.info("Director with id={} successfully deleted", directorId);
        } else {
            log.warn("Director with id={} was not deleted", directorId);
        }
        return result;
    }

    @Override
    public Director add(Director director) {
        log.info("Received request to add a director: {}", director);
        if ((director = directorDao.add(director)) != null) {
            log.info("New director: {} was successfully added", director);
        }
        return director;
    }

    @Override
    public Director update(Director director) {
        log.info("Received request to update a director with id={}", director.getId());
        if ((director = directorDao.update(director)) != null) {
            log.info("Director with id={} was successfully updated", director.getId());
        }
        return director;
    }
}
