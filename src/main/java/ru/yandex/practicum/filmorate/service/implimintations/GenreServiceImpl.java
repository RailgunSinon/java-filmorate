package ru.yandex.practicum.filmorate.service.implimintations;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.FilmGenre;
import ru.yandex.practicum.filmorate.service.interfaces.SimpleService;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

@Service
@Slf4j
public class GenreServiceImpl implements SimpleService<FilmGenre> {

    private final GenreStorage genreStorage;

    public GenreServiceImpl(
        @Qualifier("inDatabaseGenreStorageImpl") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Override
    public FilmGenre getDataById(int id) {
        log.debug("Запрос на получение жанра по id");
        return genreStorage.getGenreById(id);
    }

    @Override
    public List<FilmGenre> getAllData() {
        log.debug("Запрос на получение всех жанров");
        return genreStorage.getAllGenres();
    }
}
