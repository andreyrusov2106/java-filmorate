package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FriendsStorage {
    void addFriend(Long user_id, Long friend_id);
    void removeFriend(Long user_id, Long friend_id);
    List<User> getAllFriends(Long user_id);
    List<User> getCommonFriends(Long user_id, Long friend_id);
}
