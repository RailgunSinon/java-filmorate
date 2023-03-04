package ru.yandex.practicum.filmorate.models;

import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    @PositiveOrZero
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;


    public Film(int id, Film film){
        this.id = id;
        this.name = film.name;
        this.description = film.description;
        this.releaseDate = film.releaseDate;
        this.duration = film.duration;
    }
}
