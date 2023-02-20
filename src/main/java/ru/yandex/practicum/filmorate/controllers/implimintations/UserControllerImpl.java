package ru.yandex.practicum.filmorate.controllers.implimintations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.validation.Valid;
import javax.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.controllers.interfaces.UserController;
import ru.yandex.practicum.filmorate.exeptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exeptions.CustomValidationException;
import ru.yandex.practicum.filmorate.exeptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.User;

//Я по тихому начинаю ненавидеть ТЗ яндекса. Сначала напиши - а потом подгоняй под тесты постмана
//Мы используем PUT но при это при отсутствии пользователя должны возвращать ошибку. Хотя есть PATCH
//Это смешно даже уже.

@RestController
public class UserControllerImpl implements UserController {

    private final HashMap<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserControllerImpl.class);
    private int counter = 1;

    @Override
    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) {
        try {
            log.debug("Получен запрос на добавления пользователя");
            if (users.containsKey(user.getId())) {
                log.error("Попытка создания существующего пользователя");
                throw new UserAlreadyExistsException("Пользователь с таким id уже существует!");
            }
            userValidation(user);
            if (!StringUtils.hasText(user.getName())) {
                log.info("Имя пользователя было пустым и заменено логином");
                user.setName(user.getLogin());
            }
            user = new User(counter++, user);
            log.debug("Пользователь успешно создан", user);
            users.put(user.getId(), user);
            return users.get(user.getId());
        } catch (ValidationException | CustomValidationException exception) {
            log.error("Не пройдена валидация для создания пользователя", user);
            throw new CustomValidationException(exception.getMessage());
        }
    }

    @Override
    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Получен запрос на обновление пользователя");
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с таким id не найден");
        }
        try {
            userValidation(user);
            log.debug("Пользователь успешно обновлен", user);
            users.put(user.getId(), user);
            return users.get(user.getId());
        } catch (ValidationException | CustomValidationException exception) {
            log.error("Не пройдена валидация для обновления пользователя", user);
            throw new CustomValidationException(exception.getMessage());
        }
    }

    @Override
    @GetMapping("/users")
    public List<User> getAllUsers() {
        log.debug("Запрос на получение всех фильмов");
        log.info("Всего пользователей : " + users.size());
        return new ArrayList<User>(users.values());
    }

    private void userValidation(User user) {
        if (user.getLogin().contains(" ")) {
            throw new CustomValidationException("Логин содержит пробелы!");
        }
    }
}
