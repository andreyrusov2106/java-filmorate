package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends;

    public void addFriend(long id) {
        if (friends == null) friends = new HashSet<>();
        friends.add(id);
    }

    public void removeFriend(long id) {
        if (friends != null) friends.remove(id);
    }

}
