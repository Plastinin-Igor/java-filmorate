package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;

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
    public FilmDto create(@Valid @RequestBody NewFilmRequest filmRequest) {
        FilmDto filmLocal = filmService.addFilm(filmRequest);
        log.info("Добавлен фильм {} с идентификатором {}.", filmLocal.getName(), filmLocal.getId());
        return filmLocal;
    }


    @PutMapping("films")
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest filmRequest) {
        FilmDto film = filmService.updateFilm(filmRequest);
        log.info("Фильм {} (id: {}) изменен.", film.getName(), film.getId());
        return film;
    }

    @GetMapping("films")
    public Collection<FilmDto> findAll() {
        log.info("Выполнен запрос к списку фильмов. Найдено фильмов {}.", filmService.getFilms().size());
        return filmService.getFilms();
    }

    @GetMapping("films/{filmId}")
    public FilmDto findById(@PathVariable Long filmId) {
        log.info("Выполнен запрос к фильму с id {}.", filmId);
        return filmService.getFilmById(filmId);
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
    public Collection<FilmDto> getTopPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        if (count <= 0) {
            log.error("Параметр count имеет отрицательное значение");
            throw new ParameterNotValidException("Count", "Параметр имеет отрицательное значение.");
        }
        log.info("Выполнен запрос топ-{} фильмов.", count);
        return filmService.getTopPopularFilms(count);
    }

    // Жанры
    @GetMapping("/genres")
    public Collection<GenreDto> getAllGenres() {
        log.info("Выполнен запрос жанров");
        return filmService.getAllGenres();
    }

    // Жанр по id
    @GetMapping("/genres/{id}")
    public GenreDto getGenreById(@PathVariable Long id) {
        log.info("Выполнен запрос к жанру с id: {}.", id);
        return filmService.getGenreById(id);
    }

    // Рейтинги mpa
    @GetMapping("/mpa")
    public Collection<RatingDto> getAllRatings() {
        log.info("Выполнен запрос рейтингов mpa");
        return filmService.getAllRatings();
    }

    //Рейтинг mpa по id
    @GetMapping("/mpa/{id}")
    public RatingDto getRatingById(@PathVariable Long id) {
        log.info("Выполнен запрос к рейтенку с id: {}.", id);
        return filmService.getRatingById(id);
    }

}
