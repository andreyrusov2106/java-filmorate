package ru.yandex.practicum.filmorate.storage.likes;

import java.util.List;

public interface LikeStorage {

    void addFilmLike(Long idFilm, Long idUser);

    void removeFilmLike(Long idFilm, Long idUser);

    List<Long> getAllLikes(Long idFilm);
}
