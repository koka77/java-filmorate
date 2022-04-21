package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
@ToString
@AllArgsConstructor
public class Film {

    private int id;

    @NonNull
    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;
    //
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate releaseDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Duration filmDuration;
}
