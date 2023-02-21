package ru.yandex.practicum.filmorate.controllers.interfaces;

import java.util.List;
import ru.yandex.practicum.filmorate.models.Film;

//Много полезных комментариев. Спасибо за ответы большое)
public interface FilmController {
    Film addFilm(Film film);
    Film updateFilm(Film film);
    List<Film> getAllFilms();
}
