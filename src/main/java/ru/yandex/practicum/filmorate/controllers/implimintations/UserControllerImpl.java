package ru.yandex.practicum.filmorate.controllers.implimintations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.validation.Valid;
import javax.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.controllers.interfaces.UserController;
import ru.yandex.practicum.filmorate.exeptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exeptions.CustomValidationException;
import ru.yandex.practicum.filmorate.models.User;

//Я по тихому начинаю ненавидеть ТЗ яндекса. Сначала напиши - а потом подгоняй под тесты постмана

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
            userValidation(user);
            if (user.getName().equals("")) {
                log.info("Имя пользователя было пустым и заменено логином");
                user.setName(user.getLogin());
            }
            if (!users.containsKey(user.getId())) {
                user = new User(counter,user);
                counter++;
                log.debug("Пользователь успешно создан", user);
                return users.put(user.getId(), user);
            } else {
                log.error("Попытка создания существующего пользователя");
                throw new UserAlreadyExistsException("Пользователь с таким id уже существует!");
            }
        } catch (ValidationException | CustomValidationException exception) {
            log.error("Не пройдена валидация для создангия пользователя", user);
            throw new CustomValidationException(exception.getMessage());
        }
    }

    @Override
    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Получен запрос на обновление пользователя");
        try {
            userValidation(user);
            if (!users.containsKey(user.getId())) {
                user = new User(counter,user);
                counter++;
                log.debug("Пользователь успешно создан", user);
            } else {
                log.debug("Пользователь успешно обновлен", user);
            }
            return users.put(user.getId(), user);

        } catch (ValidationException | CustomValidationException exception) {
            log.error("Не пройдена валидация для создангия пользователя", user);
            throw new CustomValidationException(exception.getMessage());
        }
    }

    @Override
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return new ArrayList<User>(users.values());
    }

    private void userValidation(User user) {
        if (user.getLogin().contains(" ")) {
            throw new CustomValidationException("Логин содержит пробелы!");
        }
    }
}
