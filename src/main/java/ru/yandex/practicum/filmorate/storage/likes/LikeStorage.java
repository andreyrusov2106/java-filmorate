package ru.yandex.practicum.filmorate.storage.likes;

import java.util.List;

public interface LikeStorage {

    void addFilmLike(Long film_id, Long user_id);

    void removeFilmLike(Long film_id, Long user_id);

    List<Long> getAllLikes(Long idFilm);
}
