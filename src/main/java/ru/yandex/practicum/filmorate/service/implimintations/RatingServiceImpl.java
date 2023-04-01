package ru.yandex.practicum.filmorate.service.implimintations;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.service.interfaces.SimpleService;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingStorage;

@Service
@Slf4j
public class RatingServiceImpl implements SimpleService<FilmRating> {

    private final RatingStorage ratingStorage;

    public RatingServiceImpl(
        @Qualifier("inDatabaseRatingStorageImpl") RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }


    @Override
    public FilmRating getDataById(int id) {
        log.debug("Запрос на получение возрастного рейтинга фильма по id");
        return ratingStorage.getRatingById(id);
    }

    @Override
    public List<FilmRating> getAllData() {
        log.debug("Запрос на получение всех рейтингов");
        return ratingStorage.getAllRatings();
    }
}
