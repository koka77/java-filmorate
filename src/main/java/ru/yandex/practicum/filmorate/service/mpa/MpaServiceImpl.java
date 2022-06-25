package ru.yandex.practicum.filmorate.service.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.jdbc.MpaDao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MpaServiceImpl implements MpaService {

    private final MpaDao mpaDao;

    @Autowired
    public MpaServiceImpl(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    @Override
    public Optional<Mpa> getById(int id) {
        return mpaDao.getById(id);
    }

    @Override
    public List<Mpa> getAll() {
        return mpaDao.getAll();
    }
}
