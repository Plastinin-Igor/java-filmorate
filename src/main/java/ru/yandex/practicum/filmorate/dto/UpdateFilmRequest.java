package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
public class UpdateFilmRequest {
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза должна быть задана")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма не может быть меньше нуля")
    private int duration;

    private RatingDto mpa;

    private LinkedHashSet<GenreDto> genres;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return !(releaseDate == null);
    }

    public boolean hasMpa() {
        return !(mpa == null);
    }

    public boolean hasGenres() {
        return !(genres == null);
    }

    @AssertTrue(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    public boolean isReleaseDateValid() {
        LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);
        return releaseDate.isAfter(cinemaBirthday);
    }

}
