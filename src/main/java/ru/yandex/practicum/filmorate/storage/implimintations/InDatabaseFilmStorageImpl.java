package ru.yandex.practicum.filmorate.storage.implimintations;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmGenre;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.Storage;

@Component
@Slf4j
public class InDatabaseFilmStorageImpl implements Storage<Film> {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;

    public InDatabaseFilmStorageImpl(JdbcTemplate jdbcTemplate,
        @Qualifier("inDatabaseGenreStorageImpl") GenreStorage genreStorage,
        @Qualifier("inDatabaseRatingStorageImpl") RatingStorage ratingStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
    }

    @Override
    public Film addData(Film film) {
        String sqlQueryToFilms = "INSERT INTO FILMS (id,name,description,releaseDate,duration,"
            + "rating) VALUES (?,?,?,?,?,?)";
        String sqlQueryToFilmGenres = "INSERT INTO FilmGenres (filmId,genreId) VALUES (?,?)";
        String sqlQueryToLikes = "INSERT INTO Likes (filmId,userId) VALUES (?,?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQueryToFilms);
            stmt.setInt(1, film.getId());
            stmt.setString(2, film.getName());
            stmt.setString(3, film.getDescription());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(5, film.getDuration());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        });

        for (FilmGenre genre : film.getGenres()) {
            jdbcTemplate.update(sqlQueryToFilmGenres, film.getId(), genre.getId());
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
            stmt.setInt(5, film.getMpa().getId());
            stmt.setInt(6, film.getId());
            return stmt;
        });

        jdbcTemplate.update(sqlQueryToLikesDelete, film.getId());
        jdbcTemplate.update(sqlQueryToFilmGenresDelete, film.getId());

        for (FilmGenre genre : film.getGenres()) {
            jdbcTemplate.update(sqlQueryToFilmGenresInsert, film.getId(), genre.getId());
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

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("releaseDate").toLocalDate();
        int duration = resultSet.getInt("duration");
        FilmRating rating = ratingStorage.getRatingById(resultSet.getInt("rating"));
        ArrayList<FilmGenre> filmGenre = getAllFilmGenres(id);
        HashSet<Integer> likesSet = getFilmLikes(id);

        Film film = new Film(id, name, description, releaseDate, duration, filmGenre, rating,
            likesSet);

        return film;

    }

    private ArrayList<FilmGenre> getAllFilmGenres(int id) {
        String sqlQuery = "SELECT genreId FROM FilmGenres WHERE filmId = ?";
        return new ArrayList<>(genreStorage.getAllGenresForFilm(
            new ArrayList<>(jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> rs.getInt("genreId"), id))));
    }

    private HashSet<Integer> getFilmLikes(int id) {
        String sqlQuery = "SELECT userId FROM Likes WHERE filmId = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery,
            (rs, rowNum) -> rs.getInt("userId"), id));
    }

}
