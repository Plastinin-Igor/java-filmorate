package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setRating(film.getRating() != null ? RatingMapper.mapToRatingDto(film.getRating()) : null);
        dto.setGenres(film.getGenres() != null ? film.getGenres().stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toCollection(LinkedHashSet::new)) : new LinkedHashSet<>());

        return dto;
    }

}
