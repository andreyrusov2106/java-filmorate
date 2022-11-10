package ru.yandex.practicum.filmorate.exceptions;

public class ResourceAlreadyExistException extends RuntimeException {
    public ResourceAlreadyExistException(final String message) {
        super(message);
    }
}
