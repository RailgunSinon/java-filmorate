package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exeptions.CustomValidationException;
import ru.yandex.practicum.filmorate.exeptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmGenre;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.service.implimintations.FilmServiceImpl;
import ru.yandex.practicum.filmorate.storage.implimintations.InMemoryFilmStorageImpl;

@SpringBootTest
public class FilmControllerTests {

    private ValidatorFactory validatorFactory;
    private Validator validator;
    private FilmController filmController;
    private Film film;

    @BeforeEach
    public void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
        filmController = new FilmController(new FilmServiceImpl(new InMemoryFilmStorageImpl()));
        ArrayList<FilmGenre> genre = new ArrayList<>();
        genre.add(new FilmGenre(1, "Комедия"));
        genre.add(new FilmGenre(2, "Драма"));
        film = new Film(1, "Тихоокеанский рубеж", "О роботах",
            LocalDate.of(2013, 6, 11), 131, new ArrayList<>(),
            new FilmRating(1, "G"), new HashSet<>());
        film.setGenres(genre);
    }

    @Test
    void validateIdNegativeShouldFailValidation() {
        film = new Film(-1, "Тихоокеанский рубеж", "О роботах",
            LocalDate.of(2013, 6, 11), 131, new ArrayList<>(),
            new FilmRating(1, "G"), new HashSet<>());

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
    }

    @Test
    void validateIdZeroShouldNotFailValidation() {
        film = new Film(0, "Тихоокеанский рубеж", "О роботах",
            LocalDate.of(2013, 6, 11), 131, new ArrayList<>(),
            new FilmRating(1, "G"), new HashSet<>());

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(0, violations.size());
    }

    @Test
    void validateDurationNegativeShouldFailValidation() {
        film = new Film(1, "Тихоокеанский рубеж", "О роботах",
            LocalDate.of(2013, 6, 11), -131, new ArrayList<>(),
            new FilmRating(1, "G"), new HashSet<>());

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
    }

    @Test
    void validateDurationZeroShouldFailValidation() {
        film = new Film(1, "Тихоокеанский рубеж", "О роботах",
            LocalDate.of(2013, 6, 11), 0, new ArrayList<>(),
            new FilmRating(1, "G"), new HashSet<>());

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
    }

    @Test
    void validateNameEmptyShouldFailValidation() {
        film.setName(" ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
    }

    @Test
    void validateDescriptionEmptyShouldFailValidation() {
        film.setDescription(" ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
    }

    @Test
    void addFilmWithBadDescriptionShouldThrowCustomValidationException() {
        film.setDescription("Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        final CustomValidationException exception = Assertions.assertThrows(
            CustomValidationException.class, new Executable() {
                @Override
                public void execute() {
                    filmController.addFilm(film);
                }
            });

        Assertions.assertEquals("Длинна описания больше 200 символов!",
            exception.getMessage());
    }

    @Test
    void addFilmWithBadReleaseDateShouldThrowCustomValidationException() {
        film.setReleaseDate(LocalDate.of(1800, 12, 14));

        final CustomValidationException exception = Assertions.assertThrows(
            CustomValidationException.class, new Executable() {
                @Override
                public void execute() {
                    filmController.addFilm(film);
                }
            });

        Assertions.assertEquals("Дата релиза до 28.12.1985!", exception.getMessage());
    }

    @Test
    void getAllFilmsTestShouldReturnArrayListOfFilms() {
        ArrayList<Film> films;
        filmController.addFilm(film);

        films = (ArrayList<Film>) filmController.getAllFilms();

        Assertions.assertEquals(1, films.size());
    }

    @Test
    void addFilmTestShouldAddFilm() {
        filmController.addFilm(film);

        Assertions.assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void updateFilmTestShouldAddFilm() {
        Film gottenFilm;
        filmController.addFilm(film);
        film = new Film(1, "Тихоокеанский рубеж 2", "О роботах",
            LocalDate.of(2013, 6, 11), 131, new ArrayList<>(),
            new FilmRating(1, "G"), new HashSet<>());

        filmController.updateFilm(film);
        gottenFilm = filmController.getAllFilms().get(0);

        Assertions.assertEquals("Тихоокеанский рубеж 2", gottenFilm.getName());
    }

    @Test
    void updateFilmWithBadParametersShouldThrowCustomValidationException() {
        filmController.addFilm(film);
        film = new Film(1, "Тихоокеанский рубеж 2", "О роботах",
            LocalDate.of(1800, 6, 11), 131, new ArrayList<>(),
            new FilmRating(1, "G"), new HashSet<>());

        final CustomValidationException exception = Assertions.assertThrows(
            CustomValidationException.class, new Executable() {
                @Override
                public void execute() {
                    filmController.updateFilm(film);
                }
            });

        Assertions.assertEquals("Дата релиза до 28.12.1985!", exception.getMessage());
    }

    @Test
    void updateFilmNotExistParametersShouldThrowFilmNotFoundException() {
        filmController.addFilm(film);
        film = new Film(5, "Тихоокеанский рубеж 2", "О роботах",
            LocalDate.of(1900, 6, 11), 131, new ArrayList<>(),
            new FilmRating(1, "G"), new HashSet<>());

        final FilmNotFoundException exception = Assertions.assertThrows(
            FilmNotFoundException.class, new Executable() {
                @Override
                public void execute() {
                    filmController.updateFilm(film);
                }
            });

        Assertions.assertEquals("Фильм с таким id не найден", exception.getMessage());
    }

}
