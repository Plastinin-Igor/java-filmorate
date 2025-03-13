package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;
    private final Map<Long, Set<Long>> likes;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
        likes = new HashMap<>();
    }

    public Film addFilm(Film film) {
        film.setId(getNextId());
        film.setRating(0);
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
    public Optional<Film> getFilmById(Long filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        if (!likes.containsKey(filmId)) {
            likes.put(filmId, new HashSet<>());
            likes.get(filmId).add(userId);
            Film film = films.get(filmId);
            film.setRating(likes.get(filmId).size());
        } else {
            likes.get(filmId).add(userId);
            Film film = films.get(filmId);
            film.setRating(likes.get(filmId).size());
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        likes.get(filmId).remove(userId);
        Film film = films.get(filmId);
        film.setRating(likes.get(filmId).size());
    }

    @Override
    public Collection<Film> getTopPopularFilms(int count) {
        List<Film> filmOrderRate = new ArrayList<>(films.values());
        Collections.sort(filmOrderRate, Comparator.comparing(Film::getRating).reversed());
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

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
