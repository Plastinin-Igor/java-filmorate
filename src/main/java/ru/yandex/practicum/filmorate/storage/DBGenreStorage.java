package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    private static final String FIND_ALL_GENRE = "select g.genre_id, g.genre_name from genre g order by g.genre_id";
    private static final String FIND_GENRE_BY_ID = "select g.genre_id, g.genre_name from genre g where g.genre_id = ?";
    private static final String INSERT_FILM_GENRE = "insert into film_genre (film_id, genre_id) values(?, ?)";
    private static final String DELETE_GENRE_BY_FILM = "delete from film_genre where film_id = ?";

    public DBGenreStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> getFilmGenres(long filmId) {
        return findMany(FIND_GENRE_BY_FILM_ID, filmId);
    }

    public List<Genre> getAllGenre() {
        return findMany(FIND_ALL_GENRE);
    }

    public Genre getGenreById(long genreId) {
        try {
            return findOne(FIND_GENRE_BY_ID, genreId)
                    .orElseThrow(() -> new NotFoundException("Жанр с id: " + genreId + " не найден в системе"));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public void addFilmGenre(long filmId, long genreId) {
        insert(INSERT_FILM_GENRE, filmId, genreId);
    }

    public void deleteGenreByFilm(long filmId) {
        delete(DELETE_GENRE_BY_FILM, filmId);
    }

}
