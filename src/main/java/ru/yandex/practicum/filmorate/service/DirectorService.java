package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.validator.DirectorValidator;
import ru.yandex.practicum.filmorate.service.validator.Validator;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import javax.validation.ValidationException;
import java.util.Collection;

@Slf4j
@Service
public class DirectorService {

    private final DirectorStorage storage;
    private final Validator<Director> directorValidator;

    @Autowired
    public DirectorService(@Qualifier("DirectorDbStorage") DirectorStorage storage, DirectorValidator directorValidator) {
        this.storage = storage;
        this.directorValidator = directorValidator;
    }

    public Director getDirectorById(Long directorId) {
        return storage.findById(directorId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Director not found with id: %s", directorId)));
    }

    public Collection<Director> getAll() {
        return storage.findAll();
    }

    public Director addDirector(Director director) {
        directorValidator.check(director);
        Long directorId = storage.insert(director);
        if (directorId == null) {
            throw new ValidationException(String.format("Cannot create director: %s", director.getName()));
        }
        return getDirectorById(directorId);
    }

    public Director updateDirector(Director director) {
        directorValidator.check(director);
        getDirectorById(director.getId());
        if (!storage.update(director)) {
            throw new ValidationException(String.format("Cannot update director: %s", director.getName()));
        }
        return getDirectorById(director.getId());
    }

    public boolean deleteDirector(Long directorId) {
        return storage.delete(directorId);
    }
}
