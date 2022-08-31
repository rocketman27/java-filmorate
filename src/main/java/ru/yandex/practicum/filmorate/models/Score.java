package ru.yandex.practicum.filmorate.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
public class Score {
    private Long userId;
    private Long filmId;
    @Min(value = 1)
    @Max(value = 10)
    private Integer score;
}
