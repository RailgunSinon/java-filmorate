package ru.yandex.practicum.filmorate.exeptions;

public class CustomValidationException extends RuntimeException {

    public CustomValidationException(String message) {
        super(message);
    }
}
