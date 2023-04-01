package ru.yandex.practicum.filmorate;

import java.util.List;
import lombok.RequiredArgsConstructor;
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
import ru.yandex.practicum.filmorate.models.FilmGenre;
import ru.yandex.practicum.filmorate.storage.implimintations.InDatabaseGenreStorageImpl;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class GenreIntegrateDataBaseTests {

    private EmbeddedDatabase database;
    private JdbcTemplate jdbcTemplate;
    private GenreStorage genreStorage;

    @BeforeEach
    public void setUp() {
        database = new EmbeddedDatabaseBuilder()
            .addScript("schema.sql")
            .addScript("data.sql")
            .setType(EmbeddedDatabaseType.H2)
            .build();
        jdbcTemplate = new JdbcTemplate(database);
        genreStorage = new InDatabaseGenreStorageImpl(jdbcTemplate);
    }

    @Test
    void getGenreByIdShouldReturnGenre() {
        FilmGenre genre = genreStorage.getGenreById(1);
        Assertions.assertEquals("Комедия", genre.getName());
    }

    @Test
    void getAllGenresShouldReturnAllGenresList() {
        List<FilmGenre> filmGenres = genreStorage.getAllGenres();
        Assertions.assertEquals(6, filmGenres.size());
    }
}
