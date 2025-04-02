package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DBGenreStorage;
import ru.yandex.practicum.filmorate.storage.DbRatingStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DBGenreStorage genreStorage;
    private final DbRatingStorage ratingStorage;

    //Добавление фильм
    public FilmDto addFilm(NewFilmRequest newFilmRequest) {
        Film film = FilmMapper.mapToFilm(newFilmRequest);
        film = filmStorage.addFilm(film);
        return FilmMapper.mapToFilmDto(film);
    }

    //Изменение фильма
    public FilmDto updateFilm(UpdateFilmRequest filmRequest) {
        filmExists(filmRequest.getId());
        Film film = filmStorage.getFilmById(filmRequest.getId()).get();
        film = FilmMapper.updateFilmFields(film, filmRequest);
        film = filmStorage.updateFilm(film);
        return FilmMapper.mapToFilmDto(film);
    }

    //Удаление фильма
    public void deleteFilm(Long filmId) {
        filmExists(filmId);
        filmStorage.deleteFilm(filmId);
    }

    //Список всех фильмов
    public Collection<FilmDto> getFilms() {
        return filmStorage.getFilms()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    //Получение фильма по id
    public FilmDto getFilmById(Long filmId) {
        filmExists(filmId);
        return FilmMapper.mapToFilmDto(filmStorage.getFilmById(filmId).get());
    }

    //Пользователь ставит лайк фильму.
    public void addLike(Long filmId, Long userId) {

        if (!userStorage.isUserExists(userId)) {
            log.error("Пользователь с Id: {} не найден в системе.", userId);
            throw new NotFoundException("Пользователь с Id: " + userId + " не найден в системе.");
        }
        filmExists(filmId);
        filmStorage.addLike(filmId, userId);
    }

    // Пользователь удаляет лайк.
    public void deleteLike(Long filmId, Long userId) {
        //likeExists(filmId, userId);
        filmStorage.deleteLike(filmId, userId);
    }

    //Возвращает список из первых count фильмов по количеству лайков.
    public Collection<FilmDto> getTopPopularFilms(int count) {
        return filmStorage.getTopPopularFilms(count)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    // Список жанров
    public Collection<GenreDto> getAllGenres() {
        return genreStorage.getAllGenre()
                .stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    // Жанр по id
    public GenreDto getGenreById(long genreId) {
        return GenreMapper.mapToGenreDto(genreStorage.getGenreById(genreId));
    }

    // Список рейтингов mpa
    public Collection<RatingDto> getAllRatings() {
        return ratingStorage.getRatings()
                .stream()
                .map(RatingMapper::mapToRatingDto)
                .collect(Collectors.toList());
    }

    // Рейтинг mpa по id
    public RatingDto getRatingById(long ratingId) {
        return RatingMapper.mapToRatingDto(ratingStorage.getRatingById(ratingId));
    }

    //Проверить наличие фильма в хранилище
    private void filmExists(Long id) {
        if (!filmStorage.isFilmExists(id)) {
            log.error("Фильм с Id: {} не найден в системе.", id);
            throw new NotFoundException("Фильм с Id: " + id + " не найден в системе.");
        }
    }

    //Проверить наличие лайка в хранилище
    private void likeExists(Long filmId, Long userId) {
        if (!filmStorage.isLikeExists(filmId, userId)) {
            log.error("Лайк пользователя {} для фильма {} не найден.", filmId, userId);
            throw new NotFoundException("Лайк пользователя " + filmId + " для фильма " + userId + " не найден.");
        }
    }

}
