package ru.yandex.practicum.filmorate.controllers.storage.implimintations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controllers.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.models.Film;

@Component
@Slf4j
public class InMemoryFilmStorageImpl implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<Film>(films.values());
    }
}
