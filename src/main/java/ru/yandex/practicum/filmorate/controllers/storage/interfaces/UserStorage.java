package ru.yandex.practicum.filmorate.controllers.storage.interfaces;

import java.util.List;
import ru.yandex.practicum.filmorate.models.User;

public interface UserStorage {
    User addUser(User user);
    User updateUser(User user);
    List<User> getAllUsers();
}
