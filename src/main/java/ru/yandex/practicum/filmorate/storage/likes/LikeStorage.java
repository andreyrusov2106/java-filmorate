package ru.yandex.practicum.filmorate.storage.likes;

import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LikeStorage {

    void addFilmLike(Long idFilm, Long idUser);

    void removeFilmLike(Long idFilm, Long idUser);

    List<Long> getAllLikes(Long idFilm);
}
