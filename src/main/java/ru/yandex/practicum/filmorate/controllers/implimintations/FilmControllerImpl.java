package ru.yandex.practicum.filmorate.controllers.implimintations;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.controllers.interfaces.FilmController;
import ru.yandex.practicum.filmorate.exeptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exeptions.CustomValidationException;
import ru.yandex.practicum.filmorate.exeptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.service.interfaces.FilmService;


@RestController
@Slf4j
public class FilmControllerImpl implements FilmController {

    private static final int TOP_FILM_COUNT = 10;
    private final FilmService filmService;

    @Autowired
    public FilmControllerImpl(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("Получен запрос на добавления фильма");
        return filmService.addFilm(film);
    }

    @Override
    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Получен запрос на обновление фильма");
        return filmService.updateFilm(film);
    }

    @Override
    @GetMapping("/films")
    public List<Film> getAllFilms() {
        log.debug("Запрос на получение всех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.debug("Запрос на получение фильма по id");
        return filmService.getFilmById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLikeFromFilm(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getMostPopularFilm(@RequestParam(required = false) Integer count) {
        return filmService.getMostPopularFilms(count == null ? TOP_FILM_COUNT : count);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException exception) {
        log.error("Не пройдена валидация для создания пользователя");
        return Map.of("Ошибка валидации", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(
        final CustomValidationException exception) {
        log.error("Не пройдена валидация для создания пользователя");
        return Map.of("Ошибка валидации", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleFilmAlreadyExistsException(
        final FilmAlreadyExistsException exception) {
        log.error("Попытка создать существующий фильм");
        return Map.of("Фильм уже существует", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleFilmNotFoundException(final FilmNotFoundException exception) {
        log.error("Фильм с заданным id не был обнаружен");
        return Map.of("Не удалось найти указанный фильм", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleLikeNotFoundException(final LikeNotFoundException exception) {
        log.error("Лайк у фильма не был обнаружен");
        return Map.of("Не удалось найти оценку пользователя", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleRunTimeException(final RuntimeException exception) {
        log.error("Неизвестная ошибка");
        return Map.of("Что-то пошло не так", exception.getMessage());
    }
}
