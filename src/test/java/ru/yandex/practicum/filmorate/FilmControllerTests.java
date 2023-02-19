package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
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
import ru.yandex.practicum.filmorate.controllers.implimintations.FilmControllerImpl;
import ru.yandex.practicum.filmorate.controllers.interfaces.FilmController;
import ru.yandex.practicum.filmorate.exeptions.CustomValidationException;
import ru.yandex.practicum.filmorate.models.Film;

//Мне тут -наныли- сказали, что нижнее подчеркивание в имени метода лучше не использовать. Аля не
// по феншую. Хотя по мне более читаемо. Насколько это критично именно в тестах. Ибо по идее тест
//Это всё тот же метод, по нотации да - не стоит, но всё же.
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
        filmController = new FilmControllerImpl();
        film = new Film(1, "Тихоокеанский рубеж", "О роботах",
            LocalDate.of(2013, 6, 11), 131);
    }

    @Test
    void validateIdNegativeShouldFailValidation() {
        film = new Film(-1, "Тихоокеанский рубеж", "О роботах",
            LocalDate.of(2013, 6, 11), 131);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
    }

    @Test
    void validateIdZeroShouldNotFailValidation() {
        film = new Film(0, "Тихоокеанский рубеж", "О роботах",
            LocalDate.of(2013, 6, 11), 131);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(0, violations.size());
    }

    @Test
    void validateDurationNegativeShouldFailValidation() {
        film = new Film(1, "Тихоокеанский рубеж", "О роботах",
            LocalDate.of(2013, 6, 11), -131);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
    }

    @Test
    void validateDurationZeroShouldFailValidation() {
        film = new Film(1, "Тихоокеанский рубеж", "О роботах",
            LocalDate.of(2013, 6, 11), 0);

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
            LocalDate.of(2013, 6, 11), 131);

        filmController.updateFilm(film);
        gottenFilm = filmController.getAllFilms().get(0);

        Assertions.assertEquals("Тихоокеанский рубеж 2", gottenFilm.getName());
    }

    @Test
    void updateFilmWithBadParametersShouldThrowCustomValidationException() {
        filmController.addFilm(film);
        film = new Film(1, "Тихоокеанский рубеж 2", "О роботах",
            LocalDate.of(1800, 6, 11), 131);

        final CustomValidationException exception = Assertions.assertThrows(
            CustomValidationException.class, new Executable() {
                @Override
                public void execute() {
                    filmController.updateFilm(film);
                }
            });

        Assertions.assertEquals("Дата релиза до 28.12.1985!", exception.getMessage());
    }
}
