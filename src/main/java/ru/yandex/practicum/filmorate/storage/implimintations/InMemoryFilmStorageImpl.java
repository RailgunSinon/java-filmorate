package ru.yandex.practicum.filmorate.storage.implimintations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.Storage;

@Component
@Slf4j
public class InMemoryFilmStorageImpl implements Storage<Film> {

    private final HashMap<Integer, Film> films = new HashMap<>();

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
        return new ArrayList<Film>(films.values());
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
}
