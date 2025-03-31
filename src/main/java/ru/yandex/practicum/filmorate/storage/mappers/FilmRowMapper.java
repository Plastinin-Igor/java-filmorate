package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));

        Timestamp releaseDate = resultSet.getTimestamp("releasedate");
        film.setReleaseDate(releaseDate.toLocalDateTime().toLocalDate());

        film.setDuration(resultSet.getInt("duration"));

        film.setRating(Rating
                .builder()
                .ratingId(resultSet.getLong("rating_id"))
                .rating(resultSet.getString("rating"))
                .description(resultSet.getString("rating_description"))
                .build()
        );

        return film;
    }
}
