package ru.yandex.practicum.filmorate.models;

import java.time.LocalDate;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Ну допустим у нас будет регистрация ребенка, который только родился =) (по ТЗ)
//А можно почитать где-нибудь как создать кастомную аннотацию для валидации, скажем, логина?
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @PositiveOrZero
    private int id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;

    public User(int id,User user){
        this.id = id;
        this.email = user.email;
        this.login = user.login;
        this.name = user.name;
        this.birthday = user.birthday;
    }
}
