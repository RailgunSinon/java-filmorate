package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;
import ru.yandex.practicum.filmorate.models.Film;

public interface FilmStorage {
    Film addFilm(Film film);
    Film updateFilm(Film film);
    List<Film> getAllFilms();
    Film getFilmById(int id);
    boolean isFilmExists(int id);
}
