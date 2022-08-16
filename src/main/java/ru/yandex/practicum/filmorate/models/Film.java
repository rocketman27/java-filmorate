package ru.yandex.practicum.filmorate.models;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.AfterDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder(setterPrefix = "with")
public class Film {
    private long id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @Size(max = 200, message = "Description size cannot be more than 200 characters")
    private String description;
    @AfterDate(value = "18951228", message = "Release date should be after threshold")
    private LocalDate releaseDate;
    @Positive(message = "Duration cannot be negative")
    private int duration;
    @NotNull
    private Mpa mpa;
    private List<Genre> genres;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        if (mpa != null) {
            values.put("mpa_id", mpa.getId());
        }
        return values;
    }
}
