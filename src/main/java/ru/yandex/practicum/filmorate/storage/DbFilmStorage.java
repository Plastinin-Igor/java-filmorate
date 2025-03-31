package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
@Slf4j
@Primary
public class DbFilmStorage extends BaseDBStorage<Film> implements FilmStorage {
    private static final String FIND_BY_ID_QUERY = """
            select f.film_id,
                   f.name,
                   f.description,
                   f.releasedate,
                   f.duration,
                   r.rating_id,
                   r.rating,
                   r.description rating_description
              from film f
              left join rating r on (f.rating = r.rating_id)
             where f.film_id = ?
            """;
    private static final String FIND_ALL_QUERY = """
            select f.film_id,
                   f.name,
                   f.description,
                   f.releasedate,
                   f.duration,
                   r.rating_id,
                   r.rating,
                   r.description rating_description
              from film f
              left join rating r on (f.rating = r.rating_id)
            """;
    private static final String INSERT_QUERY_FILM = """
            insert into film
            (name, description, releasedate, duration, rating)
            values(?, ?, ?, ?, ?)
            """;

    private DBGenreStorage dbGenreStorage;

    public DbFilmStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, DBGenreStorage dbGenreStorage) {
        super(jdbc, mapper);
        this.dbGenreStorage = dbGenreStorage;
    }

    @Override
    public Film addFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public void deleteFilm(Long filmId) {

    }

    @Override
    public Collection<Film> getFilms() {
        List<Film> films = new ArrayList<>(findMany(FIND_ALL_QUERY));
        List<Film> resultFilms = new ArrayList<>();
        for (Film film : films) {
            LinkedHashSet<Genre> genres = new LinkedHashSet<>(dbGenreStorage.getFilmGenres(film.getId()));
            if (!genres.isEmpty()) {
                film.setGenres(genres);
            }
            resultFilms.add(film);
        }
        return resultFilms;
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>(dbGenreStorage.getFilmGenres(filmId));
        Film film = findOne(FIND_BY_ID_QUERY, filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));

        if (!genres.isEmpty()) {
            film.setGenres(genres);
        }
        return Optional.of(film);
    }

    @Override
    public void addLike(Long filmId, Long userId) {

    }

    @Override
    public void deleteLike(Long filmId, Long userId) {

    }

    @Override
    public Collection<Film> getTopPopularFilms(int count) {
        return List.of();
    }

    @Override
    public boolean isFilmExists(Long filmId) {
        return (findOne(FIND_BY_ID_QUERY, filmId).isPresent());
    }

    @Override
    public boolean isLikeExists(Long filmId, Long userId) {
        return false;
    }
}
