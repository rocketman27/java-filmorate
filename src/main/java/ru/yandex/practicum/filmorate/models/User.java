package ru.yandex.practicum.filmorate.models;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@Builder(setterPrefix = "with")
public class User {
    private long id;
    @NotBlank(message = "email is mandatory, cannot be blank")
    @Email(message = "email should be valid")
    private String email;
    @NotNull(message = "login cannot be null")
    @NotBlank(message = "login cannot be blank")
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
