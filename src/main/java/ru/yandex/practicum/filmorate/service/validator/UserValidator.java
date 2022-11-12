package ru.yandex.practicum.filmorate.service.validator;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;

@Service
@Validated
public class UserValidator implements  Validator<User> {
    @Override
    public void check(@Valid User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}
