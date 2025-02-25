package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {

    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    private LocalDate releaseDate;

    private Duration duration;

    @AssertTrue(message = "дата релиза не может быть раньше 28 декабря 1895 года")
    public boolean isReleaseDateValid() {
        LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);
        return releaseDate.isAfter(cinemaBirthday);
    }

    @AssertTrue(message = "Продолжительность фильма не может быть меньше нуля")
    public boolean isDurationValid() {
        return duration.isPositive();
    }

}
