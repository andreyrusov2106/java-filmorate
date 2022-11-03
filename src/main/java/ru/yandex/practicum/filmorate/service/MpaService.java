package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MpaService {
    private final MpaDbStorage genreDbStorage;

    @Autowired
    public MpaService(MpaDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public List<Mpa> getAllMpa() {
        return genreDbStorage.getAllMpa();
    }

    public Mpa getMpa(long idMpa) {
        Optional<Mpa> mpa = genreDbStorage.getMpa(idMpa);
        if (mpa.isPresent()) {
            return mpa.get();
        } else {
            log.warn("Mpa not found" + idMpa);
            throw new ResourceNotFoundException("Mpa not found");
        }
    }
}
