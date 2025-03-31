package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
@Slf4j
public class DBGenreStorage extends BaseDBStorage<Genre> {

    private static final String FIND_GENRE_BY_FILM_ID = """
            select g.genre_id, g.genre_name
              from genre g
              inner join film_genre fg on (g.genre_id = fg.genre_id)
              where fg.film_id = ?
            """;

    public DBGenreStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> getFilmGenres(long filmId) {
        return findMany(FIND_GENRE_BY_FILM_ID, filmId);
    }

}
