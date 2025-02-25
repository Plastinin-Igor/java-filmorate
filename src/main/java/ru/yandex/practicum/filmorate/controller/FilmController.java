package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.*;


@Service
@Validated
@RestController
@RequestMapping("films")
public class FilmController {
    private final static Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FilmController.class);

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
     * @return ResponseEntity Film
     */

    @PostMapping
    public ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        log.setLevel(Level.DEBUG);
        log.debug("Получили новый фильм для добавления {}", film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("Добавление фильма успешно выполнено.");
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
            return ResponseEntity.internalServerError().body(newFilm);
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
