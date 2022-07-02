package ru.yandex.practicum.filmorate.service.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.jdbc.DirectorDao;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class DirectorServiceImpl implements DirectorService {
    private final DirectorDao storage;

    @Autowired
    public DirectorServiceImpl(@Qualifier("DirectorDaoImpl") DirectorDao storage) {
        this.storage = storage;
    }

    @Override
    public Director getObjectById(Long directorId) {
        return getObject(directorId);
    }

    @Override
    public Collection<Director> getAll() {
        return storage.findAll();
    }

    @Override
    public Director postDirector(Director director) {
        validate(director);
        Long director_id = storage.insert(director);

        if (director_id == null) {
            throw new ValidationException(String.format("Cannot create director: %s", director.getName()));
        }
        return getObject(director_id);
    }

    @Override
    public Director putDirector(Director director) {
        validate(director);
        getObject(director.getId());

        if (!storage.update(director)) {
            throw new ValidationException(String.format("Cannot update director: %s", director.getName()));
        }
        return getObject(director.getId());
    }

    @Override
    public boolean delDirector(Long directorId) {
        return storage.delete(directorId);
    }

    private Director getObject(Long directorId) {
        Optional<Director> director = storage.findById(directorId);
        if (director.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Director not found with id: %s", directorId));
        }
        return director.get();
    }

    public void validate(Director director) {
        if (director == null) {
            throw new ValidationException("Director object is empty");
        }
        if (director.getName().trim().isEmpty()) {
            throw new ValidationException("Field name is empty");
        }
    }
}
