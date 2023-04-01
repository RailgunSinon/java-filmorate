package ru.yandex.practicum.filmorate.service.implimintations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exeptions.CustomValidationException;
import ru.yandex.practicum.filmorate.exeptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exeptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.Friendship;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.service.interfaces.UserService;
import ru.yandex.practicum.filmorate.storage.interfaces.Storage;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final Storage<User> userStorage;
    private int counter = 1;

    @Autowired
    public UserServiceImpl(@Qualifier("inDatabaseUserStorageImpl") Storage<User> userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User addUser(User user) {
        log.debug("Получен запрос на добавления пользователя");
        if (userStorage.isDataExists(user.getId())) {
            log.error("Попытка создания существующего пользователя");
            throw new UserAlreadyExistsException("Пользователь с таким id уже существует!");
        }
        userValidation(user);
        if (!StringUtils.hasText(user.getName())) {
            log.info("Имя пользователя было пустым и заменено логином");
            user.setName(user.getLogin());
        }
        user = new User(counter++, user);
        userStorage.addData(user);
        return userStorage.getDataById(user.getId());
    }

    @Override
    public User updateUser(User user) {
        log.debug("Получен запрос на обновление пользователя");
        if (!userStorage.isDataExists(user.getId())) {
            throw new UserNotFoundException("Пользователь с таким id не найден");
        }
        userValidation(user);
        log.debug("Пользователь успешно обновлен", user);
        userStorage.updateData(user);
        return userStorage.getDataById(user.getId());
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Запрос на получение всех пользователей");
        return userStorage.getAllData();
    }

    @Override
    public User getUserById(int id) {
        log.debug("Запрос на получение пользователя по id");
        return userStorage.getDataById(id);
    }

    @Override
    public void addFriend(int firstUserId, int secondUserId) {
        log.debug("Запрос на добавления в друзья");
        if (!userStorage.isDataExists(firstUserId)) {
            throw new UserNotFoundException("Пользователь с таким id не найден");
        }
        if (!userStorage.isDataExists(secondUserId)) {
            throw new UserNotFoundException("Пользователь с таким id не найден");
        }
        User firstUser = userStorage.getDataById(firstUserId);
        firstUser.getFriendsSet().add(new Friendship(secondUserId, false));
        updateUser(firstUser);
    }

    @Override
    public void deleteFriend(int firstUserId, int secondUserId) {
        log.debug("Запрос на удаление друга");
        if (!userStorage.isDataExists(firstUserId)) {
            throw new UserNotFoundException("Пользователь с таким id не найден");
        }
        if (!userStorage.isDataExists(secondUserId)) {
            throw new UserNotFoundException("Пользователь с таким id не найден");
        }
        User firstUser = userStorage.getDataById(firstUserId);
        if (firstUser.getFriendsSet().contains(new Friendship(secondUserId, false))) {
            firstUser.getFriendsSet().remove(new Friendship(secondUserId, false));
        }
        updateUser(firstUser);
    }

    @Override
    public List<User> getCommonFriends(int firstUserId, int secondUserId) {
        ArrayList<User> commonFriends = new ArrayList<>();
        HashSet<Friendship> firstUserSet = userStorage.getDataById(firstUserId).getFriendsSet();
        HashSet<Friendship> secondUserSet = userStorage.getDataById(secondUserId).getFriendsSet();
        if (firstUserSet.size() > secondUserSet.size()) {
            for (Friendship friend : firstUserSet) {
                if (secondUserSet.contains(friend)) {
                    commonFriends.add(userStorage.getDataById(friend.getFriendId()));
                }
            }
        } else {
            for (Friendship friend : secondUserSet) {
                if (firstUserSet.contains(friend)) {
                    commonFriends.add(userStorage.getDataById(friend.getFriendId()));
                }
            }
        }

        return commonFriends;
    }

    @Override
    public List<User> getUserFriends(int userId) {
        ArrayList<User> friends = new ArrayList<>();
        User user = userStorage.getDataById(userId);

        for (Friendship friend : user.getFriendsSet()) {
            friends.add(userStorage.getDataById(friend.getFriendId()));
        }

        return friends;
    }

    private void userValidation(User user) {
        if (user.getLogin().contains(" ")) {
            throw new CustomValidationException("Логин содержит пробелы!");
        }
    }

}
