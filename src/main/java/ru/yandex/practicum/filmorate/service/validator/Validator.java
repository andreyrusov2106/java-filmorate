package ru.yandex.practicum.filmorate.service.validator;

import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface Validator<T> {
    void check(@Valid T t);
}
