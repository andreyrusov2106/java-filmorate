package ru.yandex.practicum.filmorate.storage.recommendation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationStorage {

    List<Long> getRecommendations(long id);
}
