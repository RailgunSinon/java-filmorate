package ru.yandex.practicum.filmorate.storage.implimintations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

@Component
public class InMemoryUserStorageImpl implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User addData(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User updateData(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public List<User> getAllData() {
        return new ArrayList<User>(users.values());
    }

    @Override
    public User getDataById(int id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с таким id не найден");
        }
        return users.get(id);
    }

    @Override
    public boolean isDataExists(int id) {
        return users.containsKey(id);
    }
}
