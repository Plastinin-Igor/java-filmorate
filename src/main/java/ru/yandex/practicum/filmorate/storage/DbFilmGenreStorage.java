package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

@Repository
public class DbFilmGenreStorage extends BaseDBStorage<FilmGenre> {

    private static final String FIND_ALL_FILM_GENRE_RELATION = """
            select fg.film_id,
            	   g.genre_id,
            	   g.genre_name
              from film_genre fg
              inner join genre g on (g.genre_id = fg.genre_id)
            """;

    public DbFilmGenreStorage(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }

    public List<FilmGenre> getAllFilmGenre() {
        return findMany(FIND_ALL_FILM_GENRE_RELATION);
    }

}
