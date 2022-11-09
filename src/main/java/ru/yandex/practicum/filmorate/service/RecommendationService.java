package ru.yandex.practicum.filmorate.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.recommendation.RecommendationDbStorage;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RecommendationService {

    private final RecommendationDbStorage recommendationDbStorage;
    private final FilmService filmService;

    public RecommendationService(RecommendationDbStorage recommendationDbStorage, FilmService filmService) {
        this.recommendationDbStorage = recommendationDbStorage;
        this.filmService = filmService;
    }

    public List<Film> getRecommendations(Long id) {
        List<Long> filmId = recommendationDbStorage.getRecommendations(id);
        List<Film> films = new ArrayList<>();
        for(Long ids : filmId) {
            films.add(filmService.getFilm(ids));
        }
        log.info("Films are recommended to you: " + films);
        return films;
    }
}