package ru.yandex.practicum.filmorate.exceptions;

public class FilmAlreadyExistException extends RuntimeException {
    public FilmAlreadyExistException(final String message) {
        super(message);
    }
}
