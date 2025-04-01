package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
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
        dto.setMpa(film.getRating() != null ? RatingMapper.mapToRatingDto(film.getRating()) : null);
        dto.setGenres(film.getGenres() != null ? film.getGenres().stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toCollection(LinkedHashSet::new)) : new LinkedHashSet<>());

        return dto;
    }

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());

        film.setRating(request.getMpa() != null ? RatingMapper.mapToRating(request.getMpa()) : null);

        film.setGenres(request.getGenres() != null ? request.getGenres().stream()
                .map(GenreMapper::mapToGenre)
                .collect(Collectors.toCollection(LinkedHashSet::new)) : new LinkedHashSet<>());

        return film;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasMpa()) {
            film.setRating(request.getMpa() != null ? RatingMapper.mapToRating(request.getMpa()) : null);
        }
        if (request.hasGenres()) {
            film.setGenres(request.getGenres() != null ? request.getGenres().stream()
                    .map(GenreMapper::mapToGenre)
                    .collect(Collectors.toCollection(LinkedHashSet::new)) : new LinkedHashSet<>());

        }
        film.setDuration(request.getDuration());
        return film;
    }

}
