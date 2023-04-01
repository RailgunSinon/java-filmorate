package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.ArrayList;
import java.util.List;
import ru.yandex.practicum.filmorate.models.FilmGenre;

public interface GenreStorage {

    List<FilmGenre> getAllGenres();

    FilmGenre getGenreById(int id);

    List<FilmGenre> getAllGenresForFilm(ArrayList<Integer> genres);
}
