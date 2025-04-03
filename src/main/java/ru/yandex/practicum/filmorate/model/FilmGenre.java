package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmGenre {
    private Long filmId;
    private Long genreId;
    private String genreName;

    public Genre getGenre() {
        Genre genre = new Genre(genreId, genreName);
        return genre;
    }
}
