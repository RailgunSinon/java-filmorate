package ru.yandex.practicum.filmorate.storage.implimintations;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.models.FilmGenre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

//Пришлось
@Component
@Slf4j
public class InMemoryGenreStorageImpl implements GenreStorage {

    private final ArrayList<FilmGenre> genres = new ArrayList<>();

    public InMemoryGenreStorageImpl() {
        this.genres.add(new FilmGenre(1, "Комедия"));
        this.genres.add(new FilmGenre(2, "Драма"));
        this.genres.add(new FilmGenre(3, "Мультфильм"));
        this.genres.add(new FilmGenre(4, "Триллер"));
        this.genres.add(new FilmGenre(5, "Документальный"));
        this.genres.add(new FilmGenre(6, "Боевик"));
    }

    @Override
    public List<FilmGenre> getAllGenres() {
        return new ArrayList<>(genres);
    }

    @Override
    public FilmGenre getGenreById(int id) {
        for (FilmGenre genre : genres) {
            if (genre.getId() == id) {
                return genre;
            }
        }
        throw new ObjectNotFoundException("Жанр с таким id не найден");
    }


    public List<FilmGenre> getAllGenresForFilm(ArrayList<Integer> genres) {
        ArrayList<FilmGenre> result = new ArrayList<>();
        ArrayList<FilmGenre> allGenres = new ArrayList<>(getAllGenres());

        for (FilmGenre genre : allGenres) {
            if (genres.contains(genre.getId())) {
                result.add(genre);
            }
        }

        return result;
    }
}
