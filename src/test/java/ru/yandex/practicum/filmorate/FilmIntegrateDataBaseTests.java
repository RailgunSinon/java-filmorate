package ru.yandex.practicum.filmorate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmGenre;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.storage.implimintations.InDatabaseFilmStorageImpl;
import ru.yandex.practicum.filmorate.storage.implimintations.InMemoryGenreStorageImpl;
import ru.yandex.practicum.filmorate.storage.implimintations.InMemoryRatingStorageImpl;
import ru.yandex.practicum.filmorate.storage.interfaces.Storage;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class FilmIntegrateDataBaseTests {

    private Storage<Film> filmStorage;
    private EmbeddedDatabase database;
    private JdbcTemplate jdbcTemplate;
    private Film film;

    @BeforeEach
    public void setUp() {
        database = new EmbeddedDatabaseBuilder()
            .addScript("schema.sql")
            .addScript("data.sql")
            .setType(EmbeddedDatabaseType.H2)
            .build();
        jdbcTemplate = new JdbcTemplate(database);
        filmStorage = new InDatabaseFilmStorageImpl(jdbcTemplate, new InMemoryGenreStorageImpl(),
            new InMemoryRatingStorageImpl());
        ArrayList<FilmGenre> genre = new ArrayList<>();
        genre.add(new FilmGenre(1, "Комедия"));
        genre.add(new FilmGenre(2, "Драма"));
        film = new Film(1, "Тихоокеанский рубеж", "О роботах",
            LocalDate.of(2013, 6, 11), 131, new ArrayList<>(),
            new FilmRating(1, "G"), new HashSet<>());
        film.setGenres(genre);
    }

    @AfterEach
    public void cleanDB() {
        database.shutdown();
    }

    @Test
    void addAndGetFilmTestShouldAddFilmAndReturnFilm() {
        filmStorage.addData(film);

        Film gottenFilm = filmStorage.getDataById(film.getId());

        Assertions.assertEquals(film.getId(), gottenFilm.getId());
        Assertions.assertEquals(film.getName(), gottenFilm.getName());
        Assertions.assertEquals(film.getDescription(), gottenFilm.getDescription());
        Assertions.assertEquals(film.getMpa(), gottenFilm.getMpa());
        Assertions.assertEquals(film.getReleaseDate(), gottenFilm.getReleaseDate());
        Assertions.assertEquals(film.getGenres().size(), gottenFilm.getGenres().size());
        Assertions.assertEquals(film.getDuration(), gottenFilm.getDuration());
        Assertions.assertEquals(film.getLikesSet().size(), gottenFilm.getLikesSet().size());
    }

    @Test
    void updateFilmTestShouldAddFilmAndReturnUpdatedFilm() {
        ArrayList<FilmGenre> genre = new ArrayList<>();
        genre.add(new FilmGenre(1, "Комедия"));
        genre.add(new FilmGenre(2, "Драма"));
        filmStorage.addData(film);
        film = new Film(1, "Тихоокеанский рубеж", "О роботах",
            LocalDate.of(2013, 6, 11), 180, new ArrayList<>(),
            new FilmRating(1, "G"), new HashSet<>());
        film.setGenres(genre);

        filmStorage.updateData(film);
        Film gottenFilm = filmStorage.getDataById(film.getId());

        Assertions.assertEquals(180, gottenFilm.getDuration());
    }

    @Test
    void getAllFilmsTestShouldAddFilmAndReturnFilms() {
        filmStorage.addData(film);

        List<Film> films = filmStorage.getAllData();

        Assertions.assertEquals(1, films.size());
    }

    @Test
    void isDataExistsNoFilmTestsShouldReturnFalse() {
        boolean flag = filmStorage.isDataExists(film.getId());
        Assertions.assertFalse(flag);
    }

}
