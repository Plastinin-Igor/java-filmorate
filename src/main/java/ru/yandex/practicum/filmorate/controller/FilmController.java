package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
@Validated
@RestController
@RequestMapping("films")
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
     * Добавление фильма
     *
     * @param film Film
     * @return ResponseEntity Film
     */

    @PostMapping
    public ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм {} с идентификатором {}.", film.getName(), film.getId());
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
            log.info("Фильм {} изменен.", oldFilm.getName());
            return ResponseEntity.ok(oldFilm);
        } else {
            log.error("Фильм с идентификатором {} не найден.", newFilm.getId());
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
        log.info("Выполнен запрос к списку фильмов. Найдено фильмов {}.", films.size());
        return films.values();
    }

    //TODO: PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    //TODO: DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    //TODO: GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, верните первые 10


}
