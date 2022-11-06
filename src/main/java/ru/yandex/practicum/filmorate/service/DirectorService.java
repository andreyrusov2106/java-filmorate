package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class DirectorService {
    private final DirectorStorage storage;

    @Autowired
    public DirectorService(@Qualifier("DirectorDbStorage") DirectorStorage storage) {
        this.storage = storage;
    }

    public Director getById(Long directorId) {
        return getObject(directorId);
    }

    public Collection<Director> getAll() {
        return storage.findAll();
    }

    public Director addDirector(Director director) {
        if (director == null) {
            throw new ValidationException("Director object is empty");
        }
        if (director.getName().trim().isEmpty()) {
            throw new ValidationException("Field name is empty");
        }
        Long director_id = storage.insert(director);
        if (director_id == null) {
            throw new ValidationException(String.format("Cannot create director: %s", director.getName()));
        }
        return getObject(director_id);
    }

    public Director updateDirector(Director director) {
        if (director == null) {
            throw new ValidationException("Director object is empty");
        }
        if (director.getName().trim().isEmpty()) {
            throw new ValidationException("Field name is empty");
        }
        getObject(director.getId());
        if (!storage.update(director)) {
            throw new ValidationException(String.format("Cannot update director: %s", director.getName()));
        }
        return getObject(director.getId());
    }

    public boolean deleteDirector(Long directorId) {
        return storage.delete(directorId);
    }

    private Director getObject(Long directorId) {
        Optional<Director> director = storage.findById(directorId);
        if (director.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Director not found with id: %s", directorId));
        }
        return director.get();
    }
}
