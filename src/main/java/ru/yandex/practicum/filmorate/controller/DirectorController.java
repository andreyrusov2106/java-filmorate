package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService service;

    @Autowired
    public DirectorController(DirectorService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Director> getAll() {
        log.info("getAll");
        return service.getAll();
    }

    @GetMapping("{id}")
    public Director getDirector(@PathVariable Long id) {
        log.info(String.format("getDirector for id: %s", id));
        return service.getDirectorById(id);
    }

    @PostMapping
    public Director postDirector(@Valid @RequestBody Director director) {
        log.info(String.format("postDirector for object: %s", director));
        return service.addDirector(director);
    }

    @PutMapping
    public Director putDirector(@Valid @RequestBody Director director) {
        log.info(String.format("putDirector for object: %s", director));
        return service.updateDirector(director);
    }

    @DeleteMapping("{id}")
    public void removeDirector(@PathVariable Long id) {
        log.info(String.format("delDirector for id: %s", id));
        service.deleteDirector(id);
    }
}
