package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

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
    private static final String UPDATE_FILM_BY_ID = """
            update film
               set name = ?,
                   description = ?,
            	   releasedate = ?,
            	   duration = ?,
            	   rating = ?
             where film_id = ?
            """;
    private static final String QUERY_TOP_N_FILMS = """
            select film_id,
                   name,
                   description,
                   releasedate,
                   duration,
                   rating_id,
                   rating,
                   rating_description
              from (select (select count(1)
                              from likes l
                             where l.film_id = f.film_id) likes_qnt,
                           f.film_id,
                           f.name,
                           f.description,
                           f.releasedate,
                           f.duration,
                           r.rating_id,
                           r.rating,
                           r.description rating_description
                      from film f
                      left join rating r
                        on (f.rating = r.rating_id)
                     order by likes_qnt desc) limit ?
            """;
    private static final String DELETE_FILM_BY_ID = "delete from film where film_id = ?";
    private static final String INSERT_QUERY_LIKE = "insert into likes (film_id, user_id) values(?, ?)";
    private static final String DELETE_LIKE_BY_ID = "delete from likes where film_id = ? and user_id = ?";
    private static final String COUNT_ROWS_FILM = "select count(*) from film";
    private static final String COUNT_LIKE_BY_FILM = "select count(*) from likes where film_id = ?";

    private final DBGenreStorage dbGenreStorage;
    private final DbRatingStorage dbRatingStorage;

    public DbFilmStorage(JdbcTemplate jdbc,
                         RowMapper<Film> mapper,
                         DBGenreStorage dbGenreStorage,
                         DbRatingStorage dbRatingStorage) {
        super(jdbc, mapper);
        this.dbGenreStorage = dbGenreStorage;
        this.dbRatingStorage = dbRatingStorage;
    }


    @Override
    public Film addFilm(Film film) {
        // Считаем из базы рейтинг mpa по его id
        long ratingId = 0;
        if (film.getRating() != null) {
            Rating rating = dbRatingStorage.getRatingById(film.getRating().getId());
            film.setRating(rating);
            ratingId = rating.getId();
        }
        // Добавим новый фильм
        long id = insert(INSERT_QUERY_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                ratingId
        );
        // Запишем полученный из базы id добавленного фильма
        film.setId(id);
        // Если есть информация о жанре(ах), добавим их в базу
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                dbGenreStorage.getGenreById(genre.getId());
                dbGenreStorage.addFilmGenre(film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Rating rating = dbRatingStorage.getRatingById(film.getRating().getId());
        update(UPDATE_FILM_BY_ID,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                rating.getId(),
                film.getId()
        );
        return film;
    }

    @Override
    public void deleteFilm(Long filmId) {
        update(DELETE_FILM_BY_ID, filmId);
    }

    @Override
    public Collection<Film> getFilms() {
        List<Film> films = new ArrayList<>(findMany(FIND_ALL_QUERY));
        for (Film film : films) {
            LinkedHashSet<Genre> genres = new LinkedHashSet<>(dbGenreStorage.getFilmGenres(film.getId()));
            if (!genres.isEmpty()) {
                film.setGenres(genres);
            }
        }
        return films;
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
        insert(INSERT_QUERY_LIKE, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        delete(DELETE_LIKE_BY_ID, filmId, userId);
    }

    @Override
    public Collection<Film> getTopPopularFilms(int count) {
        int countRowsFromFilm = getCountFromTab(COUNT_ROWS_FILM);
        List<Film> films = new ArrayList<>(findMany(QUERY_TOP_N_FILMS, Math.min(count, countRowsFromFilm)));
        for (Film film : films) {
            LinkedHashSet<Genre> genres = new LinkedHashSet<>(dbGenreStorage.getFilmGenres(film.getId()));
            if (!genres.isEmpty()) {
                film.setGenres(genres);
            }
        }
        return films;
    }

    public int getCountOfLikeByFilmId(long filmId) {
        return getCountFromTab(COUNT_LIKE_BY_FILM, filmId);
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
