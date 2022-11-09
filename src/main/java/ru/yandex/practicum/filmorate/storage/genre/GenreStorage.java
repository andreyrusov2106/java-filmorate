package ru.yandex.practicum.filmorate.storage.genre;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;
import java.util.Optional;

@Repository
public interface GenreStorage {

    List<Genre> getAllGenres();

    Optional<Genre> getGenre(long idGenre);

    List<Genre> getFilmGenres(Long idFilm);

    void addFilmGenre(long idFilm, long idGenre);

    void removeFilmAllGenre(long idFilm);

}
