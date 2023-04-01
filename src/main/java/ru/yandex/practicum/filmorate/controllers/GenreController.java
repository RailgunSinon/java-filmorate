package ru.yandex.practicum.filmorate.controllers;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exeptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.models.FilmGenre;
import ru.yandex.practicum.filmorate.service.interfaces.SimpleService;

@RestController
@Slf4j
public class GenreController {

    private final SimpleService<FilmGenre> genreSimpleService;

    @Autowired
    public GenreController(
        SimpleService<FilmGenre> genreSimpleService) {
        this.genreSimpleService = genreSimpleService;
    }

    @GetMapping("/genres")
    public List<FilmGenre> getAllFilmGenres() {
        log.debug("Запрос на получение всех жанров фильмов");
        return genreSimpleService.getAllData();
    }

    @GetMapping("/genres/{id}")
    public FilmGenre getFilmGenreById(@PathVariable int id) {
        log.debug("Запрос на получение жанра фильма по id");
        return genreSimpleService.getDataById(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleObjectNotFoundException(
        final ObjectNotFoundException exception) {
        log.error("Объект с заданным id не был обнаружен");
        return Map.of("Не удалось найти указанный объект", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleRunTimeException(final RuntimeException exception) {
        log.error("Неизвестная ошибка");
        return Map.of("Что-то пошло не так", exception.getMessage());
    }
}
