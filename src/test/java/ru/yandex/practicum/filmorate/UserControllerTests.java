package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.implimintations.UserControllerImpl;
import ru.yandex.practicum.filmorate.controllers.interfaces.UserController;
import ru.yandex.practicum.filmorate.exeptions.CustomValidationException;
import ru.yandex.practicum.filmorate.exeptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.service.implimintations.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.implimintations.InMemoryUserStorageImpl;

@SpringBootTest
public class UserControllerTests {


    private ValidatorFactory validatorFactory;
    private Validator validator;
    private UserController userController;
    private User user;

    @BeforeEach
    public void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
        userController = new UserControllerImpl(new UserServiceImpl(new InMemoryUserStorageImpl()));
        user = new User(1, "test@yandex.ru", "Kitty", "Elena",
            LocalDate.of(1996, 11, 23),new HashSet<>());
    }

    @Test
    void validateLoginEmptyShouldFailValidation() {
        user.setLogin(" ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
    }

    @Test
    void validateEmailEmptyShouldFailValidation() {
        user.setEmail("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
    }

    @Test
    void validateEmailShouldFailValidation() {
        user.setEmail("test");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
    }

    @Test
    void validateBirthdayFutureDateShouldFailValidation() {
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
    }

    @Test
    void validateBirthdayTodayShouldNotFailValidation() {
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size());
    }

    @Test
    void validateIdNegativeShouldFailValidation() {
        user = new User(-1, "test@yandex.ru", "Kitty", "Elena",
            LocalDate.of(1996, 11, 23),new HashSet<>());

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
    }

    @Test
    void validateIdZeroShouldNotFailValidation() {
        user = new User(0, "test@yandex.ru", "Kitty", "Elena",
            LocalDate.of(1996, 11, 23),new HashSet<>());

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size());
    }

    @Test
    void addUserWithBadLoginShouldThrowCustomValidationException() {
        user.setLogin("Hello world");

        final CustomValidationException exception = Assertions.assertThrows(
            CustomValidationException.class, new Executable() {
                @Override
                public void execute() {
                    userController.addUser(user);
                }
            });

        Assertions.assertEquals("Логин содержит пробелы!", exception.getMessage());
    }

    @Test
    void getAllUsersTestShouldReturnArrayListOfUsers() {
        ArrayList<User> users;
        userController.addUser(user);

        users = (ArrayList<User>) userController.getAllUsers();

        Assertions.assertEquals(1, users.size());
    }

    @Test
    void addUserTestShouldAddUser() {
        userController.addUser(user);

        Assertions.assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void updateUserTestShouldAddUser() {
        User gottenUser;
        userController.addUser(user);
        user = new User(1, "testTwo@yandex.ru", "Kitty", "Elena",
            LocalDate.of(1996, 11, 23),new HashSet<>());

        userController.updateUser(user);
        gottenUser = userController.getAllUsers().get(0);

        Assertions.assertEquals("testTwo@yandex.ru", gottenUser.getEmail());
    }

    @Test
    void updateUserWithBadParametersShouldThrowCustomValidationException() {
        userController.addUser(user);
        user = new User(1, "testTwo@yandex.ru", "Hello world", "Elena",
            LocalDate.of(1996, 11, 23),new HashSet<>());

        final CustomValidationException exception = Assertions.assertThrows(
            CustomValidationException.class, new Executable() {
                @Override
                public void execute() {
                    userController.updateUser(user);
                }
            });

        Assertions.assertEquals("Логин содержит пробелы!", exception.getMessage());
    }

    @Test
    void updateUserNotExistsShouldThrowUserNotFoundException() {
        userController.addUser(user);
        user = new User(5, "testTwo@yandex.ru", "Helloworld", "Elena",
            LocalDate.of(1996, 11, 23),new HashSet<>());

        final UserNotFoundException exception = Assertions.assertThrows(
            UserNotFoundException.class, new Executable() {
                @Override
                public void execute() {
                    userController.updateUser(user);
                }
            });

        Assertions.assertEquals("Пользователь с таким id не найден", exception.getMessage());
    }

}
