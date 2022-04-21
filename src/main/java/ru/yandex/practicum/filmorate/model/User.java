package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@Builder
@ToString
@NoArgsConstructor
public class User {

    private int id;

    @Email
    @NonNull
    @NotBlank
    private String email;

    @NonNull
    @NotBlank
    @Pattern(regexp = "\\S+")
    private String login;

    private String name;

    @PastOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate birthday;

    public User(int id, @Valid String email, @Valid String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name.isEmpty() || name.isBlank() ? login : name;
        this.birthday = birthday;
    }
}
