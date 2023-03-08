package ru.yandex.practicum.filmorate.exeptions;

public class LikeNotFoundException extends RuntimeException {
    public LikeNotFoundException(String message) {
        super(message);
    }
}
