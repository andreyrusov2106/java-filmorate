package ru.yandex.practicum.filmorate.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.recommendation.RecommendationDbStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationDbStorage recommendationDbStorage;
    private final FilmService filmService;

    public List<Film> getRecommendations(Long id) {
        List<Long> filmId = recommendationDbStorage.getRecommendations(id);
        List<Film> films = new ArrayList<>();
        for(Long ids : filmId) {
            films.add(filmService.getFilm(ids));
        }
        return films;
    }
}