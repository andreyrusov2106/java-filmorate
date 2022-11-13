package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component()
@Qualifier("FriendDbStorage")
@RequiredArgsConstructor
public class FriendDbStorage implements FriendsStorage {

    private final JdbcTemplate jdbcTemplate;

    private final static String DELETE_FRIEND_BY_ID_SQL =
            "DELETE FROM FRIENDSHIP " +
                    "WHERE USER_ID = ? AND FRIEND_ID = ?";
    private final static String SELECT_ALL_FRIEND_BY_ID_SQL =
            "SELECT U.* FROM FRIENDSHIP F " +
                    "LEFT JOIN USERS U ON U.USER_ID = F.FRIEND_ID " +
                    "WHERE F.USER_ID = ?";
    private final static String SELECT_COMMON_FRIEND_SQL =
            "SELECT U.* FROM FRIENDSHIP F " +
                    "LEFT JOIN USERS U ON U.USER_ID = F.FRIEND_ID " +
                    "WHERE F.USER_ID = ? AND " +
                    "F.FRIEND_ID IN(SELECT F2.FRIEND_ID FROM FRIENDSHIP F2 WHERE F2.USER_ID = ?)";

    @Override
    public void addFriend(Long idUser, Long idFriend) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friendship")
                .usingGeneratedKeyColumns("friendship_id");
        Friend f = new Friend(idUser, idFriend);
        simpleJdbcInsert.executeAndReturnKey(f.toMap());
    }

    @Override
    public void removeFriend(Long idUser, Long idFriend) {
        jdbcTemplate.update(DELETE_FRIEND_BY_ID_SQL, idUser, idFriend);
    }

    @Override
    public List<User> getAllFriends(Long idUser) {
        return jdbcTemplate.query(SELECT_ALL_FRIEND_BY_ID_SQL, this::mapRowToUser, idUser);
    }

    @Override
    public List<User> getCommonFriends(Long idUser, Long idFriend) {
        return jdbcTemplate.query(SELECT_COMMON_FRIEND_SQL, this::mapRowToUser, idUser, idFriend);
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
