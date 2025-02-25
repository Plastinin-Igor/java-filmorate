package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Validated
@RestController
@RequestMapping("api/v1/films")
public class FilmController {
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
     * Добавлвение фильма
     *
     * @param film Film
     * @return ResponseEntity Film
     */
    @PostMapping
    public ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return ResponseEntity.ok(film);
    }

    /**
     * Обновление фильма
     *
     * @param newFilm Film
     * @return ResponseEntity Film
     */
    @PutMapping
    public ResponseEntity<Film> update(@Valid @RequestBody Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            return ResponseEntity.ok(oldFilm);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Получение списка фильмов
     *
     * @return Collection
     */
    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

}
