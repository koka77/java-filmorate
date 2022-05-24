package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friend {
    long userId;
    boolean isCross;

    public Friend(Long friendId) {
        this.userId = friendId;
    }
}
