package ru.yandex.practicum.filmorate;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.GenreController;
import ru.yandex.practicum.filmorate.models.FilmGenre;
import ru.yandex.practicum.filmorate.service.implimintations.GenreServiceImpl;
import ru.yandex.practicum.filmorate.storage.implimintations.InMemoryGenreStorageImpl;

@SpringBootTest
public class GenreControllerTests {

    private GenreController genreController;

    @BeforeEach
    public void setUp() {
        genreController = new GenreController(new GenreServiceImpl(new InMemoryGenreStorageImpl()));
    }

    @Test
    void getGenreByIdShouldReturnGenre() {
        FilmGenre genre = genreController.getFilmGenreById(1);
        Assertions.assertEquals("Комедия", genre.getName());
    }

    @Test
    void getAllGenresShouldReturnAllGenresList() {
        List<FilmGenre> filmGenres = genreController.getAllFilmGenres();
        Assertions.assertEquals(6, filmGenres.size());
    }

}
