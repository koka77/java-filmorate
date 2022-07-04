package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Director {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;

    public Director(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
