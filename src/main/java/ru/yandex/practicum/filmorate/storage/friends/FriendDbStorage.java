package ru.yandex.practicum.filmorate.storage.friends;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component()
@Qualifier("FriendDbStorage")
public class FriendDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;
    private final String DELETE_FRIEND_BY_ID_SQL =
            "DELETE FROM PUBLIC.FRIENDSHIP " +
            "WHERE USER_ID=? AND FRIEND_ID=?";
    private final String SELECT_ALL_FRIEND_BY_ID_SQL =
            "SELECT U.* FROM PUBLIC.FRIENDSHIP F " +
            "LEFT JOIN PUBLIC.USERS U ON U.USER_ID=F.FRIEND_ID " +
            "WHERE F.USER_ID=?";
    private final String SELECT_COMMON_FRIEND_SQL =
            "SELECT U.* FROM FRIENDSHIP F " +
            "LEFT JOIN PUBLIC.USERS U ON U.USER_ID=F.FRIEND_ID " +
            "WHERE F.USER_ID=? AND " +
            "F.FRIEND_ID IN(SELECT F2.FRIEND_ID FROM FRIENDSHIP F2 WHERE F2.USER_ID=?)";

    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public void addFriend(Long user_id, Long friend_id) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friendship")
                .usingGeneratedKeyColumns("friendship_id");
        Friend f = new Friend(user_id,friend_id);
        simpleJdbcInsert.executeAndReturnKey(f.toMap());
    }
    @Override
    public void removeFriend(Long user_id, Long friend_id) {
        jdbcTemplate.update(DELETE_FRIEND_BY_ID_SQL, user_id, friend_id);
    }
    @Override
    public List<User> getAllFriends(Long user_id) {
        return jdbcTemplate.query(SELECT_ALL_FRIEND_BY_ID_SQL, this::mapRowToUser,user_id );
    }
    @Override
    public List<User> getCommonFriends(Long user_id, Long friend_id) {
        return jdbcTemplate.query(SELECT_COMMON_FRIEND_SQL, this::mapRowToUser,user_id, friend_id);
    }
    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

}
