package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.model.enums.FriendshipConfirmation;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private final Map<Long, FriendshipConfirmation> friends = new HashMap<>();

    public Set<Long> getFriends() {

        return friends.keySet();
    }


    public User(Long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public void addFriend(long id) {
        friends.put(id, FriendshipConfirmation.UNCONFIRMED);
    }

    public void confirmFriend(long id) {
        friends.put(id, FriendshipConfirmation.CONFIRMED);
    }

    public void removeFriend(long id) {
        friends.remove(id);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}
