package ru.yandex.practicum.filmorate.service.implimintations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.CustomValidationException;
import ru.yandex.practicum.filmorate.exeptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exeptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.service.interfaces.FilmService;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

//Я подумывал задачь через Primary или Qualifier конкретики, но по идее Autowired должен и сам
//справиться при единичной импементации.
@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private int counter = 1;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage){
        this.filmStorage = filmStorage;
    }

    @Override
    public Film addFilm(Film film) {
        log.debug("Получен запрос на добавления фильма");
        if (filmStorage.isFilmExists(film.getId())) {
            log.error("Попытка создания существующего фильма");
            throw new FilmAlreadyExistsException("Фильм с таким id уже существует!");
        }
        filmValidation(film);
        film = new Film(counter++, film);
        filmStorage.addFilm(film);
        return filmStorage.getFilmById(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("Получен запрос на обновление фильма");
        if (!filmStorage.isFilmExists(film.getId())) {
            throw new FilmNotFoundException("Фильм с таким id не найден");
        }
        filmValidation(film);
        filmStorage.addFilm(film);
        return filmStorage.getFilmById(film.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        log.debug("Запрос на получение всех фильмов");
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(int id) {
        log.debug("Запрос на получение фильма по id");
        return filmStorage.getFilmById(id);
    }

    @Override
    public void addLikeToFilm(int filmId, int userId) {
        log.debug("Получен запрос на добавление лайка");
        Film film = filmStorage.getFilmById(filmId);
        film.getLikesSet().add(userId);
        filmStorage.updateFilm(film);
    }

    @Override
    public void deleteLikeFromFilm(int filmId, int userId) {
        log.debug("Получен запрос на удаление лайка");
        Film film = filmStorage.getFilmById(filmId);
        if( film.getLikesSet().contains(userId)){
            film.getLikesSet().remove(userId);
        } else {
            throw new LikeNotFoundException("Лайк у фильма не был обнаружен");
        }
        filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getMostPopularFilms(int counter) {
        log.debug("Получен запрос на получение популярных фильмов",counter);
        return filmStorage.getAllFilms().stream()
            .sorted((Film filmOne,Film filmTwo) ->
                filmTwo.getLikesSet().size() - filmOne.getLikesSet().size())
            .sorted(Collections.reverseOrder())
            .limit(counter)
            .collect(Collectors.toList());
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
