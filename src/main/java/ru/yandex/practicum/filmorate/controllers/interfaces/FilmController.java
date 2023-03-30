package ru.yandex.practicum.filmorate.controllers.interfaces;

import java.util.List;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmGenre;
import ru.yandex.practicum.filmorate.models.FilmRating;

public interface FilmController {
    Film addFilm(Film film);
    Film updateFilm(Film film);
    List<Film> getAllFilms();
    List<FilmGenre> getAllFilmGenres();
    List<FilmRating> getAllFilmRatings();
}
