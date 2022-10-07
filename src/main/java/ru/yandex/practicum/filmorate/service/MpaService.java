package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;

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
        Mpa mpa = genreDbStorage.getMpa(idMpa);
        if (mpa != null) {
            return mpa;
        } else {
            log.warn("Mpa not found" + idMpa);
            throw new ResourceNotFoundException("Mpa not found");
        }
    }
}
