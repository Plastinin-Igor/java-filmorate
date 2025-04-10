package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;
    private final Map<Long, Set<Long>> likes;
    private final Map<Long, Set<String>> filmGenre;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
        likes = new HashMap<>();
        filmGenre = new HashMap<>();
    }

    public Film addFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    public Film updateFilm(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        return oldFilm;
    }


    public void deleteFilm(Long filmId) {
        films.remove(filmId);
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film getFilmById(Long filmId) {
        return films.get(filmId);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        if (likes.containsKey(filmId)) {
            likes.get(filmId).add(userId);
            Film film = films.get(filmId);
        } else {
            likes.put(filmId, new HashSet<>());
            likes.get(filmId).add(userId);
            Film film = films.get(filmId);
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        likes.get(filmId).remove(userId);
        Film film = films.get(filmId);
    }

    @Override
    public Collection<Film> getTopPopularFilms(int count) {
        List<Film> filmOrderRate = new ArrayList<>(films.values());
        return filmOrderRate.subList(0, Math.min(count, filmOrderRate.size()));
    }

    @Override
    public boolean isFilmExists(Long filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public boolean isLikeExists(Long filmId, Long userId) {
        return likes.containsKey(filmId) && likes.get(filmId).contains(userId);
    }

    public void addGenreToFilm(Long filmId, String genre) {
        if (filmGenre.containsKey(filmId)) {
            filmGenre.get(filmId).add(genre);
        } else {
            filmGenre.put(filmId, new HashSet<>());
            filmGenre.get(filmId).add(genre);
        }
    }

    public void deleteGenreFromFilm(Long filmId, String genre) {
        filmGenre.get(filmId).remove(genre);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
