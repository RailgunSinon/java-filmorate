package ru.yandex.practicum.filmorate.storage.implimintations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingStorage;

@Component
@Slf4j
public class InDatabaseRatingStorageImpl implements RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    public InDatabaseRatingStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<FilmRating> getAllRatings() {
        String sqlQuery = "SELECT * FROM RATING";
        try {
            return jdbcTemplate.query(sqlQuery, this::makeFilmRating);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Рейтинг с таким id не найден");
        }
    }

    @Override
    public FilmRating getRatingById(int id) {
        String sqlQuery = "SELECT * FROM RATING WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeFilmRating, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Рейтинг с таким id не найден");
        }
    }

    private FilmRating makeFilmRating(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        return new FilmRating(id, name);
    }
}
