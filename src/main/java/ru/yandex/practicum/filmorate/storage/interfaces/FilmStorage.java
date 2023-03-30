package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmGenre;
import ru.yandex.practicum.filmorate.models.FilmRating;

//А тут снова пришлось разделить инерфейсы. Новый функционал распилил их пополам
public interface FilmStorage {

    Film addData(Film t);

    Film updateData(Film t);

    List<Film> getAllData();

    Film getDataById(int id);

    boolean isDataExists(int id);

    List<FilmGenre> getAllGenres();

    FilmGenre getFilmGenreById(int id);

    List<FilmRating> getAllFilmRatings();

    FilmRating getFilmRatingById(int id);
}
