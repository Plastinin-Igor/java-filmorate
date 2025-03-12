package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.Collection;

@Slf4j
@Service
@Validated
@RestController
@RequiredArgsConstructor
public class FilmController {
    final FilmService filmService;

    @PostMapping("films")
    public ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм {} с идентификатором {}.", film.getName(), film.getId());
        return ResponseEntity.ok(film);
    }


    @PutMapping("films")
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
    @GetMapping("films")
    public Collection<Film> findAll() {
        log.info("Выполнен запрос к списку фильмов. Найдено фильмов {}.", films.size());
        return films.values();
    }

    //TODO: PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    //TODO: DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    //TODO: GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, верните первые 10


}
