package ru.yandex.practicum.filmorate.exceptions;

public class ObjectNotFoundException extends RuntimeException{
    public ObjectNotFoundException(final String message) {
        super(message);
    }
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
