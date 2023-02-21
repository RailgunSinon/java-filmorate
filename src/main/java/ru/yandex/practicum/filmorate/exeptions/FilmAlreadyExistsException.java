package ru.yandex.practicum.filmorate.exeptions;

public class FilmAlreadyExistsException extends RuntimeException {

    public FilmAlreadyExistsException(String message) {
        super(message);
    }
}
