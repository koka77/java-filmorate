package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Feed {
    private LocalDateTime timestamp; // ": 123344556,
    private Long userId; // ": 123,
    private String eventType;// ": "LIKE", // одно из значениий LIKE, REVIEW или FRIEND
    private String operation; // ": "REMOVE", // одно из значениий REMOVE, ADD, UPDATE
    private Long eventId; // ": 1234, //primary key
    private Long entityId; // ": 1234   // идентификатор сущности, с которой произошло событие
}
