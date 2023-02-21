package ru.yandex.practicum.filmorate.controllers.implimintations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.validation.Valid;
import javax.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.controllers.interfaces.FilmController;
import ru.yandex.practicum.filmorate.exeptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exeptions.CustomValidationException;
import ru.yandex.practicum.filmorate.exeptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;

//А если я хочу не бросить ошибку, а вернуть ее, как можно обыргать возврат? Хочется вместо 500
//получать что-то более осмысленное
@RestController
public class FilmControllerImpl implements FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmControllerImpl.class);
    private int counter = 1;

    @Override
    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("Получен запрос на добавления фильма");
        if (films.containsKey(film.getId())) {
            log.error("Попытка создания существующего фильма");
            throw new FilmAlreadyExistsException("Фильм с таким id уже существует!");
        }
        try {
            filmValidation(film);
            film = new Film(counter++, film);
            log.debug("Создан объект", film);
            films.put(film.getId(), film);
            return films.get(film.getId());
        } catch (ValidationException | CustomValidationException exception) {
            log.error("Не пройдена валидация для создания фильма", film);
            throw new CustomValidationException(exception.getMessage());
        }
    }

    @Override
    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Получен запрос на обновление фильма");
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильм с таким id не найден");
        }
        try {
            filmValidation(film);
            log.debug("Обновлен объект", film);
            films.put(film.getId(), film);
            return films.get(film.getId());
        } catch (ValidationException | CustomValidationException exception) {
            log.error("Не пройдена валидация для обновления фильма", film);
            throw new CustomValidationException(exception.getMessage());
        }
    }

    @Override
    @GetMapping("/films")
    public List<Film> getAllFilms() {
        log.debug("Запрос на получение всех фильмов");
        log.info("Всего фильмов : " + films.size());
        return new ArrayList<Film>(films.values());
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
