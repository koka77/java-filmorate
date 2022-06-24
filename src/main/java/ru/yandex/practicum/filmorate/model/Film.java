package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private Set<Genre> genres;

    private Mpaa mpa;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Duration duration;

    @Builder
    public Film(Long id, @NonNull String name, String description, LocalDate releaseDate, Long duration , Mpaa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = Duration.ofSeconds(duration);
        this.likes = new HashSet<>();
    }

    public void addLike(Long filmId) {
        likes.add(filmId);
    }

    public void removeLike(Long userId) {
        likes.remove(userId);
    }
}
