package ru.yandex.practicum.filmorate.storage.mpa;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.List;
import java.util.Optional;

@Repository
public interface MpaStorage {

    List<Mpa> getAllMpa();

    Optional<Mpa> getMpa(long idMpa);
}
