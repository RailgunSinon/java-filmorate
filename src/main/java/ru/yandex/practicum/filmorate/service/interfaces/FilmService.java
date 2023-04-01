package ru.yandex.practicum.filmorate.service.interfaces;

import java.util.List;
import ru.yandex.practicum.filmorate.models.Film;

public interface FilmService {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(int id);

    void addLikeToFilm(int filmId, int userId);

    void deleteLikeFromFilm(int filmId, int userId);

    List<Film> getMostPopularFilms(int counter);

}
