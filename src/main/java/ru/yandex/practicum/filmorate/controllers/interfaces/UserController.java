package ru.yandex.practicum.filmorate.controllers.interfaces;

import java.util.List;
import ru.yandex.practicum.filmorate.models.User;

public interface UserController {
    User addUser(User user);
    User updateUser(User user);
    List<User> getAllUsers();
}
