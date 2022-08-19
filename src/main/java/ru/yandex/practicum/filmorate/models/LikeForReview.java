package ru.yandex.practicum.filmorate.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class LikeForReview {
    private boolean type;
    private long userId;
    private long reviewId;

    public boolean getType() {
        return type;
    }

}
