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
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.storage.implimintations.InDatabaseRatingStorageImpl;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingStorage;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class RatingIntegrateDataBaseTests {

    private EmbeddedDatabase database;
    private JdbcTemplate jdbcTemplate;
    private RatingStorage ratingStorage;

    @BeforeEach
    public void setUp() {
        database = new EmbeddedDatabaseBuilder()
            .addScript("schema.sql")
            .addScript("data.sql")
            .setType(EmbeddedDatabaseType.H2)
            .build();
        jdbcTemplate = new JdbcTemplate(database);
        ratingStorage = new InDatabaseRatingStorageImpl(jdbcTemplate);
    }

    @Test
    void getAllRatingsShouldReturnAllRatingsList() {
        List<FilmRating> ratings = ratingStorage.getAllRatings();
        Assertions.assertEquals(5, ratings.size());
    }

    @Test
    void getRatingByIdShouldReturnRating() {
        FilmRating rating = ratingStorage.getRatingById(1);
        Assertions.assertEquals("G", rating.getName());
    }

}
