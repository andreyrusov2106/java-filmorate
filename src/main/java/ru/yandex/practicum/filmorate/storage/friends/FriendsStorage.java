package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FriendsStorage {
    void addFriend(Long idUser, Long idFriend);

    void removeFriend(Long idUser, Long idFriend);

    List<User> getAllFriends(Long idUser);

    List<User> getCommonFriends(Long idUser, Long idFriend);
}
