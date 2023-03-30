package ru.yandex.practicum.filmorate.storage.implimintations;

import com.sun.jdi.ObjectCollectedException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmGenre;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

@Component
@Slf4j
public class InDatabaseFilmStorageImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public InDatabaseFilmStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addData(Film film) {
        String sqlQueryToFilms = "INSERT INTO FILMS (id,name,description,releaseDate,duration,"
            + "rating) VALUES (?,?,?,?,?,?)";
        String sqlQueryToFilmGenres = "INSERT INTO FilmGenres (filmId,genreId) VALUES (?,?)";
        String sqlQueryToLikes = "INSERT INTO Likes (filmId,userId)  VALUES (?,?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQueryToFilms);
            stmt.setInt(1, film.getId());
            stmt.setString(2, film.getName());
            stmt.setString(3, film.getDescription());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(5, film.getDuration());
            stmt.setInt(6, getRatingId(film.getRating()));
            return stmt;
        });

        for (String genre : film.getFilmGenre()) {
            jdbcTemplate.update(sqlQueryToFilmGenres, film.getId(), getGenreId(genre));
        }

        for (Integer like : film.getLikesSet()) {
            jdbcTemplate.update(sqlQueryToLikes, film.getId(), like);
        }

        return film;
    }

    @Override
    public Film updateData(Film film) {
        String sqlQueryToFilms = "UPDATE FILMS SET name = ?,description = ?,releaseDate = ?,"
            + "duration = ?,rating = ? WHERE id = ?";
        String sqlQueryToFilmGenresInsert = "INSERT INTO FilmGenres (filmId,genreId) VALUES (?,?)";
        String sqlQueryToLikesInsert = "INSERT INTO Likes (filmId,userId)  VALUES (?,?)";
        String sqlQueryToFilmGenresDelete = "DELETE FROM FilmGenres WHERE filmId = ?";
        String sqlQueryToLikesDelete = "DELETE FROM Likes WHERE filmId = ?";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQueryToFilms);
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, getRatingId(film.getRating()));
            stmt.setInt(6, film.getId());
            return stmt;
        });

        jdbcTemplate.update(sqlQueryToLikesDelete, film.getId());
        jdbcTemplate.update(sqlQueryToFilmGenresDelete, film.getId());

        for (String genre : film.getFilmGenre()) {
            jdbcTemplate.update(sqlQueryToFilmGenresInsert, film.getId(), getGenreId(genre));
        }

        for (Integer like : film.getLikesSet()) {
            jdbcTemplate.update(sqlQueryToLikesInsert, film.getId(), like);
        }

        return film;
    }

    @Override
    public List<Film> getAllData() {
        String sqlQuery = "SELECT * FROM FILMS";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public Film getDataById(int id) {
        String sqlQuery = "SELECT * FROM FILMS WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException("Фильм с таким id не найден");
        }
    }

    @Override
    public boolean isDataExists(int id) {
        try {
            getDataById(id);
            return true;
        } catch (FilmNotFoundException e) {
            return false;
        }
    }

    @Override
    public List<FilmGenre> getAllGenres() {
        String sqlQuery = "SELECT * FROM GENRE";
        return jdbcTemplate.query(sqlQuery, this::makeFilmGenre);
    }

    @Override
    public FilmGenre getFilmGenreById(int id) {
        String sqlQuery = "SELECT * FROM GENRE WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeFilmGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectCollectedException("Жанр с таким id не найден");
        }
    }

    @Override
    public List<FilmRating> getAllFilmRatings() {
        String sqlQuery = "SELECT * FROM RATING";
        try {
            return jdbcTemplate.query(sqlQuery, this::makeFilmRating);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectCollectedException("Рейтинг с таким id не найден");
        }
    }

    @Override
    public FilmRating getFilmRatingById(int id) {
        String sqlQuery = "SELECT * FROM RATING WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::makeFilmRating, id);
    }

    private FilmRating makeFilmRating(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        return new FilmRating(id, name);
    }

    private FilmGenre makeFilmGenre(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        return new FilmGenre(id, name);
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("releaseDate").toLocalDate();
        int duration = resultSet.getInt("duration");
        String rating = getRating(resultSet.getInt("rating"));
        HashSet<String> filmGenre = getFilmGenres(id);
        HashSet<Integer> likesSet = getFilmLikes(id);

        Film film = new Film(id, name, description, releaseDate, duration, filmGenre, rating,
            likesSet);

        return film;

    }

    private String getRating(int id) {
        String sqlQuery = "SELECT name FROM Rating WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, String.class, id);
    }

    private HashSet<String> getFilmGenres(int id) {
        String sqlQuery = "SELECT name FROM Genre WHERE id IN "
            + "(SELECT genreId FROM FilmGenres WHERE filmId = ?)";
        return new HashSet<>(jdbcTemplate.query(sqlQuery,
            (rs, rowNum) -> rs.getString("name"), id));
    }

    private HashSet<Integer> getFilmLikes(int id) {
        String sqlQuery = "SELECT userId FROM Likes WHERE filmId = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery,
            (rs, rowNum) -> rs.getInt("userId"), id));
    }

    private int getRatingId(String rating) {
        String sqlQuery = "SELECT id FROM Rating WHERE name = ?";
        return jdbcTemplate.queryForObject(sqlQuery,
            (rs, rowNum) -> rs.getInt("id"), rating);
    }

    private int getGenreId(String genre) {
        String sqlQuery = "SELECT id FROM GENRE WHERE name = ?";
        return jdbcTemplate.queryForObject(sqlQuery,
            (rs, rowNum) -> rs.getInt("id"), genre);
    }

}