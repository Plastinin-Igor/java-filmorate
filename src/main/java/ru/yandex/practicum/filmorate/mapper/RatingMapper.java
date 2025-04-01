package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;

public final class RatingMapper {

    public static RatingDto mapToRatingDto(Rating rating) {
        RatingDto dto = new RatingDto();
        dto.setId(rating.getId());
        dto.setName(rating.getName());
        return dto;
    }

    public static Rating mapToRating(RatingDto ratingDto) {
        Rating rating = new Rating();
        rating.setId(ratingDto.getId());
        rating.setName(ratingDto.getName());
        return rating;
    }
}
