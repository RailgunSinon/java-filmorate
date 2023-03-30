package ru.yandex.practicum.filmorate.storage.implimintations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmGenre;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

/*
Вообще я сначала думал сделать ENUM, но там с id могут быть усложнения и работа с памятью теперь не
в приоретете по сути.
*/
@Component
@Slf4j
public class InMemoryFilmStorageImpl implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private final ArrayList<FilmGenre> genres = new ArrayList<>();
    private final ArrayList<FilmRating> rating = new ArrayList<>();

    public InMemoryFilmStorageImpl() {
        this.rating.add(new FilmRating(1, "G"));
        this.rating.add(new FilmRating(2, "PG"));
        this.rating.add(new FilmRating(3, "PG13"));
        this.rating.add(new FilmRating(4, "R"));
        this.rating.add(new FilmRating(5, "NC17"));

        this.genres.add(new FilmGenre(1, "Комедия"));
        this.genres.add(new FilmGenre(2, "Драма"));
        this.genres.add(new FilmGenre(3, "Мультфильм"));
        this.genres.add(new FilmGenre(4, "Триллер"));
        this.genres.add(new FilmGenre(5, "Документальный"));
        this.genres.add(new FilmGenre(6, "Боевик"));
    }

    @Override
    public Film addData(Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Film updateData(Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public List<Film> getAllData() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getDataById(int id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с таким id не найден");
        }
        return films.get(id);
    }

    @Override
    public boolean isDataExists(int id) {
        return films.containsKey(id);
    }

    @Override
    public List<FilmGenre> getAllGenres() {
        return new ArrayList<>(genres);
    }

    @Override
    public FilmGenre getFilmGenreById(int id) {
        for (FilmGenre genre : genres) {
            if (genre.getId() == id) {
                return genre;
            }
        }
        throw new ObjectNotFoundException("Жанр с таким id не найден");
    }

    @Override
    public List<FilmRating> getAllFilmRatings() {
        return new ArrayList<>(rating);
    }

    @Override
    public FilmRating getFilmRatingById(int id) {
        for (FilmRating rating : rating) {
            if (rating.getId() == id) {
                return rating;
            }
        }
        throw new ObjectNotFoundException("Рейтинг с таким id не найден");
    }

}
