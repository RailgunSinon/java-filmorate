package ru.yandex.practicum.filmorate.storage.implimintations;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.Friendship;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

@Component
@Slf4j
public class InDatabaseUserStorageImpl implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public InDatabaseUserStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addData(User user) {
        String sqlQueryToUSers = "INSERT INTO USERS (id,email,login,name,birthday) "
            + "VALUES (?,?,?,?,?)";
        String sqlQueryToFriends = "INSERT INTO FRIENDS (userId,friendId,friendshipStatus) "
            + "VALUES (?,?,?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQueryToUSers);
            stmt.setInt(1, user.getId());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getLogin());
            stmt.setString(4, user.getName());
            stmt.setDate(5, Date.valueOf(user.getBirthday()));
            return stmt;
        });

        for (Friendship friend : user.getFriendsSet()) {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQueryToFriends);
                stmt.setInt(1, user.getId());
                stmt.setInt(2, friend.getFriendId());
                stmt.setInt(3, friend.isStatus() ? 1 : 0);
                return stmt;
            });
        }

        return user;
    }

    @Override
    public User updateData(User user) {
        String sqlQueryToUsers = "UPDATE USERS SET email = ?,login = ?,name = ?, birthday = ?"
            + "WHERE id = ?";
        String sqlQueryToFriends = "INSERT INTO FRIENDS (userId,friendId,friendshipStatus) "
            + "VALUES (?,?,?)";
        String sqlQueryToFriendsDelete = "DELETE FROM FRIENDS WHERE userId = ?";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQueryToUsers);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            stmt.setInt(5, user.getId());
            return stmt;
        });

        jdbcTemplate.update(sqlQueryToFriendsDelete, user.getId());

        for (Friendship friend : user.getFriendsSet()) {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQueryToFriends);
                stmt.setInt(1, user.getId());
                stmt.setInt(2, friend.getFriendId());
                stmt.setInt(3, friend.isStatus() ? 1 : 0);
                return stmt;
            });
        }

        return user;
    }

    @Override
    public List<User> getAllData() {
        String sqlQuery = "SELECT * FROM USERS";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public User getDataById(int id) {
        String sqlQuery = "SELECT * FROM USERS WHERE id = ?";
        try{
            return jdbcTemplate.queryForObject(sqlQuery, this::makeUser, id);
        } catch (EmptyResultDataAccessException e){
            throw new UserNotFoundException("Пользователь с таким id не найден");
        }
    }

    @Override
    public boolean isDataExists(int id) {
        try{
            getDataById(id);
            return true;
        } catch (UserNotFoundException e){
            return false;
        }
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String email = resultSet.getString("email");
        String login = resultSet.getString("login");
        String name = resultSet.getString("name");
        LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
        HashSet<Friendship> friendsSet = getUserFriends(id);

        User user = new User(id, email, login, name, birthday, friendsSet);
        return user;
    }

    private HashSet<Friendship> getUserFriends(int userId) {
        String sqlQuery = "SELECT * FROM Friends WHERE userId = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery,
            (rs, rowNum) -> new Friendship(rs.getInt("friendId"),
                rs.getInt("friendshipStatus") == 1 ? true : false), userId));
    }

}
