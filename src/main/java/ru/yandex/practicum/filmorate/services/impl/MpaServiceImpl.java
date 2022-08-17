package ru.yandex.practicum.filmorate.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.services.MpaService;

import java.util.List;

@Slf4j
@Service
public class MpaServiceImpl implements MpaService {
    private final MpaDao mpaDao;

    @Autowired
    public MpaServiceImpl(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    @Override
    public List<Mpa> getMpas() {
        log.info("Received request to get all MPAs");
        return mpaDao.getMpas();
    }

    @Override
    public Mpa getMpaById(long id) {
        log.info("Received request to get MPA by mpaId={}", id);
        return mpaDao.getMpaById(id);
    }
}
