package ru.yandex.practicum.filmorate.controllers.interfaces;

import java.util.List;
import ru.yandex.practicum.filmorate.models.Film;

//Пока мне не до конца ясно, почему при аннотировании методы добавления и обновления должны что-то
//возвращать

public interface FilmController {
    Film addFilm(Film film);
    Film updateFilm(Film film);
    List<Film> getAllFilms();
}
