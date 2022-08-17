package ru.yandex.practicum.filmorate.models;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.AfterDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

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
    private final Set<Genre> genres = new LinkedHashSet<>();
    private final Set<Director> directors = new LinkedHashSet<>();

    public void setGenres(Collection<Genre> genres) {
        this.genres.clear();
        this.genres.addAll(genres);
    }

    public void setDirectors(Collection<Director> directors) {
        this.directors.clear();
        this.directors.addAll(directors);
    }

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
