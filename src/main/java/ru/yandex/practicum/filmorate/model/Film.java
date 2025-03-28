package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.LinkedHashSet;

/**
 * Film.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Film {

    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза должна быть задана")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма не может быть меньше нуля")
    private int duration;

    private int rating; //TODO убрать это поле и переделать подсчет лайков у фильма

    @NotNull(message = "Рейтинг фильма должен быть задан")
    private Rating ratingMpa;

    private LinkedHashSet<Genre> genres;

    @AssertTrue(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    public boolean isReleaseDateValid() {
        LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);
        return releaseDate.isAfter(cinemaBirthday);
    }

}
