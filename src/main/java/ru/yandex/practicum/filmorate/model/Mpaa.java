package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Mpaa {
    private int id;
    private String name;

    public Mpaa(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
