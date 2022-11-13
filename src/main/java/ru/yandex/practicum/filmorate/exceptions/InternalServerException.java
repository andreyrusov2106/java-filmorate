package ru.yandex.practicum.filmorate.exceptions;

public class InternalServerException extends RuntimeException {
    public InternalServerException(int id) {
        super("Illegal Id value (" + id + ")");
    }

    public InternalServerException(String message) {
        super(message);
    }
}
