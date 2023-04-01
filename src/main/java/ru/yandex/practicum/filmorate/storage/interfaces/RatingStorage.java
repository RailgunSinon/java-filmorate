package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;
import ru.yandex.practicum.filmorate.models.FilmRating;

public interface RatingStorage {

    List<FilmRating> getAllRatings();

    FilmRating getRatingById(int id);
}
