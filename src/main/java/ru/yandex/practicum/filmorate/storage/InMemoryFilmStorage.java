package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    /**
     * Генерация уникального идентификатора
     *
     * @return long
     */
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    /**
     * Добавление фильма
     *
     * @param film Film
     * @return Film
     */
    public Film addFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    /**
     * Изменение фильма
     *
     * @param newFilm Film
     * @return Film
     */
    public Film updateFilm(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        return oldFilm;
    }

    /**
     * Удаление фильма
     *
     * @param film Film
     */
    public void deleteFilm(Film film) {
        films.remove(film.getId());
    }

    /**
     * Список всех фильмов
     *
     * @return Collection Film
     */
    public Collection<Film> findAll() {
        return films.values();
    }

}
