package ru.yandex.practicum.filmorate.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class Director {
    private long id;

    @NotBlank
    private String name;

    public Map<String, String> toMap() {
        HashMap<String, String> toReturn = new HashMap<>();
        toReturn.put("name", name);
        return toReturn;
    }
}
