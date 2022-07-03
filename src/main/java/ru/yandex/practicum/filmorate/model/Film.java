package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.AfterDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @Size(max = 200, message = "Description size cannot be more than 200 characters")
    private String description;
    @AfterDate(value = "18951228")
    private LocalDate releaseDate;
    @Positive(message = "Duration cannot be negative")
    private int duration;
}
