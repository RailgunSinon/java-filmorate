package ru.yandex.practicum.filmorate.controllers.storage.implimintations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controllers.storage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.models.User;

@Component
public class InMemoryUserStorageImpl implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<User>(users.values());
    }
}
