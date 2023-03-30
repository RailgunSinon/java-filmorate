package ru.yandex.practicum.filmorate.service.interfaces;

import java.util.List;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmGenre;
import ru.yandex.practicum.filmorate.models.FilmRating;

public interface FilmService {
    Film addFilm(Film film);
    Film updateFilm(Film film);
    List<Film> getAllFilms();
    Film getFilmById(int id);
    void addLikeToFilm(int filmId,int userId);
    void deleteLikeFromFilm(int filmId,int userId);
    List<Film> getMostPopularFilms(int counter);
    List<FilmGenre> getAllFilmGenres();
    FilmGenre getFilmGenreById(int id);
    List<FilmRating> getAllFilmRatings();
    FilmRating getFilmRatingById(int id);
}
