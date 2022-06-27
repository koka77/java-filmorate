package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Friend {
    private long userId;
    private long friendId;
    private boolean isCross;

    public Friend(Long friendId) {
        this.userId = friendId;
    }
}
