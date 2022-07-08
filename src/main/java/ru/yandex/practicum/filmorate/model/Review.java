package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class Review {
    @PositiveOrZero
    private long reviewId;
    @NotNull
    @NotBlank
    private String content;
    @NotNull
    private Boolean isPositive;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    private int useful;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("content", content);
        values.put("is_positive", isPositive);
        values.put("user_id", userId);
        values.put("film_id", filmId);
        values.put("useful", useful);

        return values;
    }

}
