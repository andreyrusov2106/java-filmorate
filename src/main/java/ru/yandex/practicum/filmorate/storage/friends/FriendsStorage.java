package ru.yandex.practicum.filmorate.storage.friends;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

@Repository
public interface FriendsStorage {

    void addFriend(Long idUser, Long idFriend);

    void removeFriend(Long idUser, Long idFriend);

    List<User> getAllFriends(Long idUser);

    List<User> getCommonFriends(Long idUser, Long idFriend);
}
