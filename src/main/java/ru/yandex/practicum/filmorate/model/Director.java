package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Director {
    private Long id;
    private String name;

    public Director(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
