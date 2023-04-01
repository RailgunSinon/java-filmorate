package ru.yandex.practicum.filmorate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.implimintations.InDatabaseUserStorageImpl;
import ru.yandex.practicum.filmorate.storage.interfaces.Storage;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserIntegrateDataBaseTests {

    private Storage<User> userStorage;
    private EmbeddedDatabase database;
    private JdbcTemplate jdbcTemplate;
    User user;

    @BeforeEach
    public void setUp() {
        database = new EmbeddedDatabaseBuilder()
            .addScript("schema.sql")
            .addScript("data.sql")
            .setType(EmbeddedDatabaseType.H2)
            .build();
        jdbcTemplate = new JdbcTemplate(database);
        userStorage = new InDatabaseUserStorageImpl(jdbcTemplate);
        user = new User(1, "test@yandex.ru", "Kitty", "Elena",
            LocalDate.of(1996, 11, 23), new HashSet<>());
    }

    @AfterEach
    public void cleanDB() {
        database.shutdown();
    }

    @Test
    void isDataExistsNoFilmTestsShouldReturnFalse() {
        boolean flag = userStorage.isDataExists(1);
        Assertions.assertFalse(flag);
    }

    @Test
    void addAndGetUserTestShouldAddFilmAndReturnUser() {
        userStorage.addData(user);

        User gottenUser = userStorage.getDataById(user.getId());

        Assertions.assertEquals(user.getId(), gottenUser.getId());
        Assertions.assertEquals(user.getLogin(), gottenUser.getLogin());
        Assertions.assertEquals(user.getEmail(), gottenUser.getEmail());
        Assertions.assertEquals(user.getFriendsSet().size(), gottenUser.getFriendsSet().size());
        Assertions.assertEquals(user.getName(), gottenUser.getName());
        Assertions.assertEquals(user.getBirthday(), gottenUser.getBirthday());
    }

    @Test
    void getAllUsersTestShouldAddFilmAndReturnUsers() {
        userStorage.addData(user);

        List<User> users = userStorage.getAllData();

        Assertions.assertEquals(1, users.size());
    }

    @Test
    void updateUserTestShouldAddFilmAndReturnUpdatedUser() {
        userStorage.addData(user);

        user = new User(1, "testTwo@yandex.ru", "Kitty", "Elena",
            LocalDate.of(1996, 11, 23), new HashSet<>());

        userStorage.updateData(user);
        User gottenUser = userStorage.getDataById(user.getId());

        Assertions.assertEquals("testTwo@yandex.ru", gottenUser.getEmail());
    }

}
