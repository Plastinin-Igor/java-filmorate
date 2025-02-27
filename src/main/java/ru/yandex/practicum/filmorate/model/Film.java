package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@AllArgsConstructor
@Builder
public class Film {

    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    private LocalDate releaseDate;

    // Duration duration;
    @Positive(message = "Продолжительность фильма не может быть меньше нуля")
    private int duration;

    @AssertTrue(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    public boolean isReleaseDateValid() {
        LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);
        return releaseDate.isAfter(cinemaBirthday);
    }

}
