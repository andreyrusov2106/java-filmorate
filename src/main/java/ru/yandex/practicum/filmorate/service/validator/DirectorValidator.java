package ru.yandex.practicum.filmorate.service.validator;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;

import javax.validation.ValidationException;

@Service
public class DirectorValidator implements Validator<Director> {
    @Override
    public void check(Director director) {
        if (director == null) {
            throw new ValidationException("Director object is empty");
        }
        if (director.getName().trim().isEmpty()) {
            throw new ValidationException("Field name is empty");
        }
    }
}
