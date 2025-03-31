package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

public final class GenreMapper {
    public static GenreDto mapToGenreDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setGenreId(genre.getGenreId());
        dto.setGenreName(genre.getGenreName());

        return dto;
    }
}
