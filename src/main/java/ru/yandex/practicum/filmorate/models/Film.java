package ru.yandex.practicum.filmorate.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film implements Comparable<Film> {

    @PositiveOrZero
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private ArrayList<FilmGenre> genres = new ArrayList<>();
    private FilmRating mpa;
    private HashSet<Integer> likesSet = new HashSet<>();


    public Film(int id, Film film) {
        this.id = id;
        this.name = film.name;
        this.description = film.description;
        this.releaseDate = film.releaseDate;
        this.duration = film.duration;
        this.mpa = film.getMpa();
        this.genres = new ArrayList<>(film.getGenres());
        this.likesSet = new HashSet<>(film.getLikesSet());
    }


    @Override
    public int compareTo(Film o) {
        if (o.getLikesSet().size() == this.getLikesSet().size()) {
            return 0;
        } else if (this.getLikesSet().size() > o.getLikesSet().size()) {
            return 1;
        } else {
            return -1;
        }
    }
}
