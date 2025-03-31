package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Film;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping("films")
    public Film create(@Valid @RequestBody Film film) {
        Film filmLocal = filmService.addFilm(film);
        log.info("Добавлен фильм {} с идентификатором {}.", film.getName(), film.getId());
        return filmLocal;
    }


    @PutMapping("films")
    public Film update(@Valid @RequestBody Film newFilm) {
        Film film = filmService.updateFilm(newFilm);
        log.info("Фильм {} (id: {}) изменен.", film.getName(), film.getId());
        return film;
    }

    @GetMapping("films")
    public Collection<FilmDto> findAll() {
        log.info("Выполнен запрос к списку фильмов. Найдено фильмов {}.", filmService.getFilms().size());
        return filmService.getFilms();
    }

    @GetMapping("films/{filmId}")
    public Film findById(@PathVariable Long filmId) {
        log.info("Выполнен запрос к фильму с id {}.", filmId);
        return filmService.getFilmById(filmId).get();
    }

    // Пользователь ставит лайк фильму.
    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable Long id,
                        @PathVariable Long userId) {
        filmService.addLike(id, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, id);
    }

    // Пользователь удаляет лайк.
    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id,
                           @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
        log.info("Пользователь {} удалил лайк фильму {}", userId, id);
    }

    //Возвращает список из первых count фильмов по количеству лайков.
    @GetMapping("/films/popular")
    public Collection<Film> getTopPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        if (count <= 0) {
            log.error("Параметр count имеет отрицательное значение");
            throw new ParameterNotValidException("Count", "Параметр имеет отрицательное значение.");
        }

        log.info("Выполнен запрос топ-{} фильмов.", count);
        return filmService.getTopPopularFilms(count);
    }

}
