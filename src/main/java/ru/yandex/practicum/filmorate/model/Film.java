package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {



    private Set<Genre> genres;

    private Integer rate; //= LikesRating.G;
    private Long id;
    final private Set<Long> likes;

    @NonNull
    @NotBlank
    private String name;

    @Size(max = 200)
    @NotBlank
    private String description;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate releaseDate;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Integer duration;

    @JsonProperty("mpa")
    private Mpa mpa;


    @Builder
    public Film(Long id, @NonNull String name, String description, LocalDate releaseDate, Integer duration , Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = new HashSet<>();
        this.mpa = mpa;
    }

    public void addLike(Long filmId) {
        likes.add(filmId);
    }

    public void removeLike(Long userId) {
        likes.remove(userId);
    }
}
