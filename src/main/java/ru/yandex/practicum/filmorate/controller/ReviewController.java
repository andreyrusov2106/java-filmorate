package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping(value = "/reviews")
    public Review create(@RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping(value = "/reviews")
    public Review update(@RequestBody Review review) {
        return reviewService.update(review);
    }

    @GetMapping(value = "/reviews/{id}")
    public Review get(@PathVariable int id) {
        return reviewService.get(id);
    }

    @DeleteMapping(value = "/reviews/{id}")
    public void delete(@PathVariable int id) {
        reviewService.delete(id);
    }

    @GetMapping(value = "/reviews")
    public List<Review> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count, @RequestParam(required = false, defaultValue = "-1") int filmId) {
        return reviewService.getAllReviews(filmId, count);
    }

    @PutMapping(value = "/reviews/{id}/like/{userId}")
    public void like(@PathVariable long id, @PathVariable long userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping(value = "/reviews/{id}/dislike/{userId}")
    public void dislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.addDisLike(id, userId);
    }

    @DeleteMapping(value = "/reviews/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping(value = "/reviews/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.removeDisLike(id, userId);
    }
}
