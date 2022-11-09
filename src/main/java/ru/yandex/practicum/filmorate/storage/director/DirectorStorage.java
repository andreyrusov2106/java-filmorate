package ru.yandex.practicum.filmorate.storage.director;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface DirectorStorage {

    Optional<Director> findById(Long directorId);

    Collection<Director> findAll();

    Long insert(Director director);

    boolean update(Director director);

    boolean delete(Long directorId);
}
