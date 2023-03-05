package ru.yandex.practicum.filmorate.service.implimintations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exeptions.CustomValidationException;
import ru.yandex.practicum.filmorate.exeptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exeptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.service.interfaces.UserService;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private int counter = 1;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User addUser(User user) {
        log.debug("Получен запрос на добавления пользователя");
        if (userStorage.isUserExists(user.getId())) {
            log.error("Попытка создания существующего пользователя");
            throw new UserAlreadyExistsException("Пользователь с таким id уже существует!");
        }
        userValidation(user);
        if (!StringUtils.hasText(user.getName())) {
            log.info("Имя пользователя было пустым и заменено логином");
            user.setName(user.getLogin());
        }
        user = new User(counter++, user);
        userStorage.addUser(user);
        return userStorage.getUserById(user.getId());
    }

    @Override
    public User updateUser(User user) {
        log.debug("Получен запрос на обновление пользователя");
        if (!userStorage.isUserExists(user.getId())) {
            throw new UserNotFoundException("Пользователь с таким id не найден");
        }
        userValidation(user);
        log.debug("Пользователь успешно обновлен", user);
        userStorage.updateUser(user);
        return userStorage.getUserById(user.getId());
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Запрос на получение всех пользователей");
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(int id) {
        log.debug("Запрос на получение пользователя по id");
        return userStorage.getUserById(id);
    }

    @Override
    public void addFriend(int firstUserId, int secondUserId) {
        log.debug("Запрос на добавления в друзья");
        User firstUser = userStorage.getUserById(firstUserId);
        User secondUser = userStorage.getUserById(secondUserId);
        firstUser.getFriendsSet().add(secondUserId);
        secondUser.getFriendsSet().add(firstUserId);
        updateUser(firstUser);
        updateUser(secondUser);
    }

    @Override
    public void deleteFriend(int firstUserId, int secondUserId) {
        log.debug("Запрос на удаление друга");
        User firstUser = userStorage.getUserById(firstUserId);
        User secondUser = userStorage.getUserById(secondUserId);
        if (firstUser.getFriendsSet().contains(secondUserId)) {
            firstUser.getFriendsSet().remove(secondUserId);
        }
        if (secondUser.getFriendsSet().contains(firstUserId)) {
            secondUser.getFriendsSet().remove(firstUserId);
        }
        updateUser(firstUser);
        updateUser(secondUser);
    }

    @Override
    public List<User> getCommonFriends(int firstUserId, int secondUserId) {
        ArrayList<User> commonFriends = new ArrayList<>();
        HashSet<Integer> firstUserSet = userStorage.getUserById(firstUserId).getFriendsSet();
        HashSet<Integer> secondUserSet = userStorage.getUserById(secondUserId).getFriendsSet();
        if (firstUserSet.size() > secondUserSet.size()) {
            for (Integer id : firstUserSet) {
                if (secondUserSet.contains(id)) {
                    commonFriends.add(userStorage.getUserById(id));
                }
            }
        } else {
            for (Integer id : secondUserSet) {
                if (firstUserSet.contains(id)) {
                    commonFriends.add(userStorage.getUserById(id));
                }
            }
        }

        return commonFriends;
    }

    @Override
    public List<User> getUserFriends(int userId) {
        ArrayList<User> friends = new ArrayList<>();
        User user = userStorage.getUserById(userId);

        for (Integer id : user.getFriendsSet()) {
            friends.add(userStorage.getUserById(id));
        }

        return friends;
    }

    private void userValidation(User user) {
        if (user.getLogin().contains(" ")) {
            throw new CustomValidationException("Логин содержит пробелы!");
        }
    }

}
