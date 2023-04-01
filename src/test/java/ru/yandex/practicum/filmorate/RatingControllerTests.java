package ru.yandex.practicum.filmorate;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.RatingController;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.service.implimintations.RatingServiceImpl;
import ru.yandex.practicum.filmorate.storage.implimintations.InMemoryRatingStorageImpl;

@SpringBootTest
public class RatingControllerTests {

    private RatingController ratingController;

    @BeforeEach
    public void setUp() {
        ratingController = new RatingController(new RatingServiceImpl(
            new InMemoryRatingStorageImpl()));
    }

    @Test
    void getAllRatingsShouldReturnAllRatingsList() {
        List<FilmRating> ratings = ratingController.getAllFilmRatings();
        Assertions.assertEquals(5, ratings.size());
    }

    @Test
    void getRatingbyIdShouldReturnRating() {
        FilmRating rating = ratingController.getFilmRatingById(1);
        Assertions.assertEquals("G", rating.getName());
    }

}
