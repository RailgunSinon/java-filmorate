package ru.yandex.practicum.filmorate.service.interfaces;

import java.util.List;
import ru.yandex.practicum.filmorate.models.User;

public interface UserService {

    User addUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserById(int id);

    void addFriend(int firstUserId, int secondUserId);

    void deleteFriend(int firstUserId, int secondUserId);

    List<User> getCommonFriends(int firstUserId, int secondUserId);

    List<User> getUserFriends(int firstUserId);
}
