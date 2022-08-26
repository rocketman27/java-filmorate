package ru.yandex.practicum.filmorate.models;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class Score {
    @Min(value = 1)
    @Max(value = 10)
    Integer score;
}
