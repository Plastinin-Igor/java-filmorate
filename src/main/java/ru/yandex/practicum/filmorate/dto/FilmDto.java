package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.Data;

import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
public class FilmDto {
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза должна быть задана")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма не может быть меньше нуля")
    private int duration;

    @NotNull(message = "Рейтинг фильма должен быть задан")
    private RatingDto rating;

    private LinkedHashSet<GenreDto> genres;
}
