package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;

public final class RatingMapper {

    public static RatingDto mapToRatingDto(Rating rating) {
        RatingDto dto = new RatingDto();
        dto.setRatingId(rating.getRatingId());
        dto.setRating(rating.getRating());
        dto.setDescription(rating.getDescription());

        return dto;
    }

    public static Rating mapToRating(RatingDto ratingDto) {
        Rating rating = new Rating();
        rating.setRatingId(ratingDto.getRatingId());
        rating.setRating(ratingDto.getRating());
        rating.setDescription(rating.getDescription());

        return rating;
    }
}
