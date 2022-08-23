package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder(setterPrefix = "with")
public class Review {

    private long reviewId;
    @NotNull
    private String content;
    @NotNull
    private Boolean isPositive;
    private int useful;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer filmId;

    public boolean getIsPositive() {
        return isPositive;
    }

}