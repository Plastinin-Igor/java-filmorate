package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class RatingDto {
    private Long ratingId;
    private String rating;
    private String description;
}
