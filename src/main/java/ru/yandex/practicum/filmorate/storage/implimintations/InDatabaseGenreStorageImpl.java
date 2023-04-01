package ru.yandex.practicum.filmorate.storage.implimintations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.models.FilmGenre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

//Сложная ситуация. Получения жанров для фильма пришлось переделать на разные запросы из-за хранения
//в памяти. Там тоже появляется этот метод, из-за интерфейса, но данные ему взять не откуда.
//А потому универсализирую сам метод - другие варианты отпали методом проб и откатов

@Component
@Slf4j
public class InDatabaseGenreStorageImpl implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public InDatabaseGenreStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<FilmGenre> getAllGenres() {
        String sqlQuery = "SELECT * FROM GENRE";
        try {
            return jdbcTemplate.query(sqlQuery, this::makeFilmGenre);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Жанр с таким id не найден");
        }
    }

    @Override
    public FilmGenre getGenreById(int id) {
        String sqlQuery = "SELECT * FROM GENRE WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeFilmGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Жанр с таким id не найден");
        }
    }

    @Override
    public List<FilmGenre> getAllGenresForFilm(ArrayList<Integer> genres) {
        ArrayList<FilmGenre> result = new ArrayList<>();
        ArrayList<FilmGenre> allGenres = new ArrayList<>(getAllGenres());

        for (FilmGenre genre : allGenres) {
            if (genres.contains(genre.getId())) {
                result.add(genre);
            }
        }

        return result;
    }

    private FilmGenre makeFilmGenre(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        return new FilmGenre(id, name);
    }
}
