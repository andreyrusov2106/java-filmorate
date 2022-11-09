package ru.yandex.practicum.filmorate.storage.user;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

@Repository
public interface UserStorage {

    User create(User user);

    User update(User user);

    User getUser(Long id);

    List<User> findAll();

    Boolean contains(User user);

    Boolean contains(Long id);

    boolean removeUser(Long id);
}
