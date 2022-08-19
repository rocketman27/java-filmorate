package ru.yandex.practicum.filmorate.models;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder(setterPrefix = "with")
public class Review {

    private  long reviewId;
    @NotNull
    private  String content;
    @NotNull
    private  Boolean isPositive;
    private  int useful;
    @NotNull
    private  Integer userId;
    @NotNull
    private  Integer filmId;
    private Set<LikeForReview> likesForReview = new HashSet<>();

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("content", content);
        values.put("is_Positive", isPositive);
        values.put("rating", useful);
        values.put("film_id", filmId);
        values.put("author_id", userId);
        return values;
    }
    public boolean getIsPositive() {
        return isPositive;
    }

    public void incrementUseful(){
        useful++;
    }
    public void decrementUseful(){
        useful--;
    }
}
