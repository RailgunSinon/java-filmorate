package ru.yandex.practicum.filmorate.service.implimintations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.CustomValidationException;
import ru.yandex.practicum.filmorate.exeptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exeptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmGenre;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.service.interfaces.FilmService;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;


@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private int counter = 1;

    @Autowired
    public FilmServiceImpl(@Qualifier("inDatabaseFilmStorageImpl") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public Film addFilm(Film film) {
        log.debug("Получен запрос на добавления фильма");
        if (filmStorage.isDataExists(film.getId())) {
            log.error("Попытка создания существующего фильма");
            throw new FilmAlreadyExistsException("Фильм с таким id уже существует!");
        }
        filmValidation(film);
        film = new Film(counter++, film);
        filmStorage.addData(film);
        return filmStorage.getDataById(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("Получен запрос на обновление фильма");
        if (!filmStorage.isDataExists(film.getId())) {
            throw new FilmNotFoundException("Фильм с таким id не найден");
        }
        filmValidation(film);
        filmStorage.updateData(film);
        return filmStorage.getDataById(film.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        log.debug("Запрос на получение всех фильмов");
        return filmStorage.getAllData();
    }

    @Override
    public Film getFilmById(int id) {
        log.debug("Запрос на получение фильма по id");
        return filmStorage.getDataById(id);
    }

    @Override
    public void addLikeToFilm(int filmId, int userId) {
        log.debug("Получен запрос на добавление лайка");
        Film film = filmStorage.getDataById(filmId);
        film.getLikesSet().add(userId);
        filmStorage.updateData(film);
    }

    @Override
    public void deleteLikeFromFilm(int filmId, int userId) {
        log.debug("Получен запрос на удаление лайка");
        Film film = filmStorage.getDataById(filmId);
        if (film.getLikesSet().contains(userId)) {
            film.getLikesSet().remove(userId);
        } else {
            throw new LikeNotFoundException("Лайк у фильма не был обнаружен");
        }
        filmStorage.updateData(film);
    }

    @Override
    public List<Film> getMostPopularFilms(int counter) {
        log.debug("Получен запрос на получение популярных фильмов", counter);
        return filmStorage.getAllData().stream()
            .sorted((Film filmOne, Film filmTwo) ->
                filmTwo.getLikesSet().size() - filmOne.getLikesSet().size())
            .sorted(Collections.reverseOrder())
            .limit(counter)
            .collect(Collectors.toList());
    }

    @Override
    public List<FilmGenre> getAllFilmGenres() {
        log.debug("Запрос на получение всех жанров");
        return filmStorage.getAllGenres();
    }

    @Override
    public FilmGenre getFilmGenreById(int id) {
        log.debug("Запрос на получение жанра фильма по id");
        return filmStorage.getFilmGenreById(id);
    }

    @Override
    public List<FilmRating> getAllFilmRatings() {
        log.debug("Запрос на получение всех рейтингов");
        return filmStorage.getAllFilmRatings();
    }

    @Override
    public FilmRating getFilmRatingById(int id) {
        log.debug("Запрос на получение возрастного рейтинга фильма по id");
        return filmStorage.getFilmRatingById(id);
    }

    private void filmValidation(Film film) {
        if (film.getDescription().length() > 200) {
            throw new CustomValidationException("Длинна описания больше 200 символов!");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new CustomValidationException("Дата релиза до 28.12.1985!");
        }
    }
}
