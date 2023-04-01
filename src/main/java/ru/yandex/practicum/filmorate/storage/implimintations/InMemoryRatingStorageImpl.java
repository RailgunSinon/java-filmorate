package ru.yandex.practicum.filmorate.storage.implimintations;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingStorage;

@Component
@Slf4j
public class InMemoryRatingStorageImpl implements RatingStorage {

    private final ArrayList<FilmRating> rating = new ArrayList<>();

    public InMemoryRatingStorageImpl() {
        this.rating.add(new FilmRating(1, "G"));
        this.rating.add(new FilmRating(2, "PG"));
        this.rating.add(new FilmRating(3, "PG13"));
        this.rating.add(new FilmRating(4, "R"));
        this.rating.add(new FilmRating(5, "NC17"));
    }

    @Override
    public List<FilmRating> getAllRatings() {
        return new ArrayList<>(rating);
    }

    @Override
    public FilmRating getRatingById(int id) {
        for (FilmRating rating : rating) {
            if (rating.getId() == id) {
                return rating;
            }
        }
        throw new ObjectNotFoundException("Возрастной рейтинг с таким id не найден");
    }
}
