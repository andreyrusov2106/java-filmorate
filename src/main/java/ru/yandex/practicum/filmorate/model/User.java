package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class User {
    private long id;
    @Email
    @NotEmpty(message = "must not be empty")
    private String email;
    @NotBlank(message = "Login is empty or contains spaces")
    @Pattern(regexp = "\\S+", message = "Login is empty or contains spaces")
    private String login;
    private String name;
    @PastOrPresent(message = "must be a date in the past or in the present")
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();

    public Set<Long> getFriends() {
        return friends;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", email);
        values.put("login", login);
        values.put("name", name);
        values.put("birthday", birthday);
        return values;
    }
}
